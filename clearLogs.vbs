On Error Resume Next
Dim fso
Set fso = CreateObject("Scripting.FileSystemObject")
logsFolder = "Snake\src\snake\resources\logs"
'Clear logs folder
fso.DeleteFolder logsFolder
fso.CreateFolder logsFolder

'Run build
Set WshShell = WScript.CreateObject("WScript.Shell")
statusCode = WshShell.Run ("build.vbs", 1, true)