On Error Resume Next
Dim fso
Set fso = CreateObject("Scripting.FileSystemObject")
execFolder = "Executable JAR"
'Copy the resources folder from /src to /dist
fso.CopyFolder "Snake/src/snake/resources", "Snake/dist/"
'Clear Executable JAR folder
fso.DeleteFolder execFolder
fso.CreateFolder execFolder
'Copy resources folder to JAR folder
fso.CopyFolder "Snake/src/snake/resources", "Executable JAR/"
'Copy JAR to JAR folder
fso.CopyFile "Snake/dist/Snake.JAR", "Executable JAR/"