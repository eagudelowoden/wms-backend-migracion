@echo off
title Sistema de Monitoreo WMS
color 0b

echo ========================================================
echo   LEVANTANDO INFRAESTRUCTURA WODEN (WMS + MONITOR)
echo ========================================================

:: 1. LEVANTAR EL MONITOR (Asegúrate de cambiar esta ruta a donde creaste el proyecto nuevo)
echo [1/2] Iniciando Dashboard en puerto 9001...
start "WMS-MONITOR-9001" cmd /k "cd /d C:\Users\Daniel\Documents\WMS\Monitor && mvnw spring-boot:run"

:: Esperamos a que el monitor este listo para recibir conexiones
echo Esperando 12 segundos a que el servidor de monitoreo cargue...
timeout /t 12 /nobreak

:: 2. LEVANTAR EL WMS BACKEND
echo [2/2] Iniciando WMS Backend en puerto 9000...
start "WMS-BACKEND-9000" cmd /k "cd /d C:\Users\Daniel\Documents\WMS\Backend && mvnw spring-boot:run"

echo.
echo ========================================================
echo   PROCESO COMPLETADO
echo   Dashboard: http://localhost:9001
echo   Health:    http://localhost:9000/actuator/health
echo ========================================================
pause