@echo off

SET BATDIR=%~dp0
PUSHD %BATDIR%

rem Customize this path as needed to point to 7zip folder
SET PATH7ZIP="D:\LiberKey\Apps\7Zip\App\7-Zip"
SET PATH=%PATH7ZIP%;%PATH%

SET /p VERSION=<..\VERSION
SET NAME=jpdfbookmarks-%VERSION%

IF EXIST %NAME% del /Q %NAME% & rmdir /S /Q %NAME%
IF EXIST %NAME%.zip del /Q %NAME%.zip
IF EXIST %NAME%.tar del /Q %NAME%.tar
IF EXIST %NAME%.tar.gz del /Q %NAME%.tar.gz

mkdir %NAME%
copy jpdfbookmarks.exe %NAME%
copy jpdfbookmarks_cli.exe %NAME%
copy link_this_in_linux_path.sh %NAME%
copy link_this_in_linux_path_cli.sh %NAME%
copy ..\jpdfbookmarks_core\dist\jpdfbookmarks.jar %NAME%
copy ..\README %NAME%
copy ..\COPYING %NAME%
copy ..\VERSION %NAME%
mkdir %NAME%\lib
copy ..\jpdfbookmarks_core\dist\lib %NAME%\lib

7z a -tzip %NAME%.zip %NAME%

7z a -ttar %NAME%.tar %NAME%
7z a -tbzip2 %NAME%.tar.gz %NAME%.tar

del /Q %NAME%.tar
del /Q %NAME%
rmdir /S /Q %NAME%

POPD
