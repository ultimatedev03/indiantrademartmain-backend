# Indian Trade Mart Backend Deployment Script (PowerShell)
# Professional deployment script for Windows environments
# Usage: .\deploy.ps1 [Command] [Options]

param(
    [Parameter(Position=0)]
    [ValidateSet("build", "start", "stop", "restart", "status", "logs", "help")]
    [string]$Command = "",
    
    [int]$Port = 8080,
    [string]$Profile = "prod",
    [string]$Heap = "-Xms512m -Xmx2g",
    [switch]$Background,
    [switch]$Help
)

# Configuration
$APP_NAME = "itech-backend"
$JAR_NAME = "itech-backend-0.0.1-SNAPSHOT.jar"
$PID_FILE = "$APP_NAME.pid"
$LOG_FILE = "$APP_NAME.log"

function Show-Usage {
    Write-Host "Usage: .\deploy.ps1 [COMMAND] [OPTIONS]" -ForegroundColor White
    Write-Host ""
    Write-Host "Commands:" -ForegroundColor Yellow
    Write-Host "  build     Build the application JAR" -ForegroundColor Green
    Write-Host "  start     Start the application" -ForegroundColor Green
    Write-Host "  stop      Stop the application" -ForegroundColor Green
    Write-Host "  restart   Restart the application" -ForegroundColor Green
    Write-Host "  status    Check application status" -ForegroundColor Green
    Write-Host "  logs      Show application logs" -ForegroundColor Green
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  -Port PORT        Server port (default: 8080)" -ForegroundColor Cyan
    Write-Host "  -Profile PROFILE  Spring profile (default: prod)" -ForegroundColor Cyan
    Write-Host "  -Heap HEAP        JVM heap settings (default: -Xms512m -Xmx2g)" -ForegroundColor Cyan
    Write-Host "  -Background       Run in background (for start command)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  .\deploy.ps1 build" -ForegroundColor Gray
    Write-Host "  .\deploy.ps1 start -Port 8081 -Profile prod -Background" -ForegroundColor Gray
    Write-Host "  .\deploy.ps1 restart -Heap '-Xms1g -Xmx4g'" -ForegroundColor Gray
}

function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] ✓ $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] ⚠ $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] ✗ $Message" -ForegroundColor Red
}

function Test-Java {
    try {
        $javaVersion = & java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
        $majorVersion = [int]($javaVersion -split '\.')[0]
        
        if ($majorVersion -lt 21) {
            Write-Error "Java 21+ is required, found Java $majorVersion"
            exit 1
        }
        
        Write-Log "Using Java $majorVersion"
        return $true
    }
    catch {
        Write-Error "Java 21+ is required but not found in PATH"
        exit 1
    }
}

function Test-Maven {
    try {
        $mvnVersion = & mvn -version 2>&1 | Select-Object -First 1
        Write-Log "Maven found: $mvnVersion"
        return $true
    }
    catch {
        Write-Error "Maven is required but not found in PATH"
        exit 1
    }
}

function Build-Application {
    Write-Log "Building $APP_NAME..."
    Test-Maven
    
    try {
        & mvn clean package -DskipTests
        
        if (Test-Path "target\$JAR_NAME") {
            $jarSize = (Get-Item "target\$JAR_NAME").Length / 1MB
            Write-Success "Build completed successfully ($([math]::Round($jarSize, 2)) MB)"
        }
        else {
            Write-Error "Build completed but JAR file not found"
            exit 1
        }
    }
    catch {
        Write-Error "Build failed: $_"
        exit 1
    }
}

function Get-ApplicationPid {
    if (Test-Path $PID_FILE) {
        return Get-Content $PID_FILE
    }
    return $null
}

function Test-ApplicationRunning {
    $pid = Get-ApplicationPid
    if ($pid) {
        try {
            $process = Get-Process -Id $pid -ErrorAction Stop
            return $true
        }
        catch {
            return $false
        }
    }
    return $false
}

function Start-Application {
    param(
        [int]$AppPort = $Port,
        [string]$AppProfile = $Profile,
        [string]$AppHeap = $Heap,
        [bool]$RunBackground = $Background
    )
    
    Test-Java
    
    if (-not (Test-Path "target\$JAR_NAME")) {
        Write-Warning "JAR file not found, building first..."
        Build-Application
    }
    
    if (Test-ApplicationRunning) {
        $pid = Get-ApplicationPid
        Write-Warning "Application is already running (PID: $pid)"
        return
    }
    
    Write-Log "Starting $APP_NAME on port $AppPort with profile $AppProfile..."
    
    # JVM Options for production
    $jvmOpts = @(
        $AppHeap -split ' '
        "-server"
        "-XX:+UseG1GC"
        "-XX:+UseContainerSupport"
        "-XX:MaxRAMPercentage=75.0"
        "-Djava.security.egd=file:/dev/./urandom"
        "-Dspring.profiles.active=$AppProfile"
        "-Dserver.port=$AppPort"
    )
    
    # Application arguments
    $appArgs = @(
        "--spring.profiles.active=$AppProfile"
        "--server.port=$AppPort"
    )
    
    try {
        if ($RunBackground) {
            $processArgs = @(
                $jvmOpts
                "-jar"
                "target\$JAR_NAME"
                $appArgs
            )
            
            $process = Start-Process -FilePath "java" -ArgumentList $processArgs -RedirectStandardOutput $LOG_FILE -RedirectStandardError $LOG_FILE -PassThru -WindowStyle Hidden
            $process.Id | Out-File -FilePath $PID_FILE -Encoding ASCII
            
            Start-Sleep -Seconds 2
            
            if (Test-ApplicationRunning) {
                $pid = Get-ApplicationPid
                Write-Success "Application started successfully (PID: $pid)"
                Write-Log "Logs: Get-Content -Path $LOG_FILE -Tail 50 -Wait"
            }
            else {
                Write-Error "Failed to start application"
                if (Test-Path $LOG_FILE) {
                    Get-Content -Path $LOG_FILE -Tail 20 | Write-Host
                }
                exit 1
            }
        }
        else {
            $processArgs = @(
                $jvmOpts
                "-jar"
                "target\$JAR_NAME"
                $appArgs
            )
            
            & java @processArgs
        }
    }
    catch {
        Write-Error "Failed to start application: $_"
        exit 1
    }
}

function Stop-Application {
    if (-not (Test-ApplicationRunning)) {
        Write-Warning "Application is not running"
        return
    }
    
    $pid = Get-ApplicationPid
    Write-Log "Stopping $APP_NAME (PID: $pid)..."
    
    try {
        # Graceful shutdown
        Stop-Process -Id $pid -Force:$false -ErrorAction Stop
        
        # Wait for graceful shutdown (max 30 seconds)
        $count = 0
        while ((Test-ApplicationRunning) -and ($count -lt 30)) {
            Start-Sleep -Seconds 1
            $count++
        }
        
        if (Test-ApplicationRunning) {
            Write-Warning "Graceful shutdown failed, forcing termination..."
            Stop-Process -Id $pid -Force -ErrorAction Stop
            Start-Sleep -Seconds 2
        }
        
        if (Test-ApplicationRunning) {
            Write-Error "Failed to stop application"
            exit 1
        }
        else {
            Write-Success "Application stopped successfully"
            if (Test-Path $PID_FILE) {
                Remove-Item $PID_FILE -Force
            }
        }
    }
    catch {
        Write-Error "Error stopping application: $_"
        exit 1
    }
}

function Show-Status {
    if (Test-ApplicationRunning) {
        $pid = Get-ApplicationPid
        Write-Success "Application is running (PID: $pid)"
        
        try {
            $process = Get-Process -Id $pid -ErrorAction Stop
            Write-Log "Process details:"
            $process | Select-Object Id, ProcessName, CPU, WorkingSet, VirtualMemorySize, StartTime | Format-Table -AutoSize
            
            # Show port binding if netstat is available
            try {
                $portInfo = netstat -ano | Select-String ":$Port " | Select-String $pid
                if ($portInfo) {
                    Write-Log "Port bindings:"
                    $portInfo | ForEach-Object { Write-Host $_.ToString() }
                }
            }
            catch {
                Write-Log "Port info not available"
            }
        }
        catch {
            Write-Warning "Could not retrieve process details"
        }
    }
    else {
        Write-Warning "Application is not running"
    }
}

function Show-Logs {
    if (Test-Path $LOG_FILE) {
        Write-Log "Showing last 50 lines of $LOG_FILE:"
        Get-Content -Path $LOG_FILE -Tail 50
    }
    else {
        Write-Warning "Log file not found: $LOG_FILE"
    }
}

# Main script logic
if ($Help -or $Command -eq "help" -or $Command -eq "") {
    Show-Usage
    exit 0
}

switch ($Command) {
    "build" {
        Build-Application
    }
    "start" {
        Start-Application -AppPort $Port -AppProfile $Profile -AppHeap $Heap -RunBackground $Background
    }
    "stop" {
        Stop-Application
    }
    "restart" {
        if (Test-ApplicationRunning) {
            Stop-Application
        }
        Start-Sleep -Seconds 2
        Start-Application -AppPort $Port -AppProfile $Profile -AppHeap $Heap -RunBackground $true
    }
    "status" {
        Show-Status
    }
    "logs" {
        Show-Logs
    }
    default {
        Write-Error "Unknown command: $Command"
        Show-Usage
        exit 1
    }
}
