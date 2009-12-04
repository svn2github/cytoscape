@if "%DEBUG%" == "" @echo off

@rem 
@rem $Revision: 2770 $ $Date: 2005-08-29 12:49:42 +0200 (Mo, 29. Aug 2005) $
@rem 

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

:begin
@rem Determine what directory it is in.
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.\

"%DIRNAME%\startGroovy.bat" "%DIRNAME%" groovy.ui.GroovyMain %*

@rem End local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" endlocal
