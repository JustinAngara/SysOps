@echo off
setlocal

REM === CONFIG ===
set BUILD_DIR=cmake-build-debug
set GENERATOR="MinGW Makefiles"
set BUILD_TYPE=Debug
set SOURCE_DIR=%~dp0

echo [INFO] Cleaning previous build directory...
rd /s /q "%BUILD_DIR%" 2>nul

echo [INFO] Configuring CMake...
cmake -G %GENERATOR% -DCMAKE_BUILD_TYPE=%BUILD_TYPE% -S . -B %BUILD_DIR%
if errorlevel 1 (
    echo [ERROR] CMake configuration failed.
    exit /b 1
)

echo [INFO] Building all targets...
cmake --build %BUILD_DIR% --target all -- -j %NUMBER_OF_PROCESSORS%
if errorlevel 1 (
    echo [ERROR] Build failed.
    exit /b 1
)

echo [INFO] Build complete.
echo [INFO] Output DLLs (if configured correctly) are in: %SOURCE_DIR%target\native

endlocal
pause
