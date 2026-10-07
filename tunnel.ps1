<#
.SYNOPSIS
    LakshanMart Public Tunnel Launcher
    Exposes your local Tomcat LakshanMart instance (port 8080) to the public internet.

.DESCRIPTION
    Supports both ngrok and zero-setup SSH tunnel (localhost.run / pinggy.io).
    Anyone anywhere in the world can access the application via the generated public URL!
#>

Param(
    [int]$Port = 8080,
    [string]$TunnelType = "auto" # "ngrok", "ssh", or "auto"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   LakshanMart - Public Online URL Tunnel Launcher        " -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Test local connectivity to port 8080
Write-Host "[1/3] Checking if local server is listening on port $Port..." -ForegroundColor Cyan
$tcpConnection = Test-NetConnection -ComputerName "127.0.0.1" -Port $Port -WarningAction SilentlyContinue
if (-not $tcpConnection.TcpTestSucceeded) {
    Write-Host "  [!] WARNING: Nothing is listening on port $Port yet." -ForegroundColor Yellow
    Write-Host "      Make sure Tomcat is started or start it with: 'catalina.bat run'" -ForegroundColor Yellow
    Write-Host "      Continuing anyway so the tunnel is ready when Tomcat boots..." -ForegroundColor Gray
} else {
    Write-Host "  [OK] Local server detected on port $Port!" -ForegroundColor Green
}

Write-Host ""
Write-Host "[2/3] Detecting available tunnel providers..." -ForegroundColor Cyan

$hasNgrok = (Get-Command "ngrok" -ErrorAction SilentlyContinue) -ne $null
$hasSsh   = (Get-Command "ssh" -ErrorAction SilentlyContinue) -ne $null

Write-Host "  ngrok installed: " -NoNewline
if ($hasNgrok) { Write-Host "YES" -ForegroundColor Green } else { Write-Host "NO" -ForegroundColor Gray }
Write-Host "  OpenSSH installed: " -NoNewline
if ($hasSsh) { Write-Host "YES" -ForegroundColor Green } else { Write-Host "NO" -ForegroundColor Gray }

Write-Host ""
Write-Host "[3/3] Launching Public Tunnel..." -ForegroundColor Cyan

if (($TunnelType -eq "ngrok" -or $TunnelType -eq "auto") -and $hasNgrok) {
    Write-Host "Starting ngrok tunnel on port $Port..." -ForegroundColor Green
    Write-Host "Your app will be accessible at: https://<your-ngrok-domain>/LakshanMart/" -ForegroundColor Yellow
    Write-Host "Press Ctrl+C to terminate the tunnel." -ForegroundColor Gray
    Write-Host ""
    ngrok http $Port
} elseif ($hasSsh) {
    Write-Host "Starting zero-configuration SSH tunnel via localhost.run..." -ForegroundColor Green
    Write-Host "No registration or token required! Connecting..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Once connected, your app will be accessible at: https://<subdomain>.lhr.life/LakshanMart/" -ForegroundColor Yellow
    Write-Host ""
    ssh -R 80:localhost:$Port nokey@localhost.run
} else {
    Write-Host "[ERROR] Neither ngrok nor ssh was detected in PATH." -ForegroundColor Red
    Write-Host "Please install ngrok via 'winget install ngrok' or use LocalTunnel via 'npm install -g localtunnel'." -ForegroundColor Yellow
}
