On Error Resume Next
'Option Explicit
Dim fso
Set fso = CreateObject("Scripting.FileSystemObject")
'Copy the resources folder from /src to /dist
fso.CopyFolder "Snake/src/snake/resources", "Snake/dist/"
'Clear Executable JAR folder
fso.DeleteFile "Executable JAR/*.*"
fso.DeleteFolder "Executable JAR/resources"
'Copy resources folder to JAR folder
fso.CopyFolder "Snake/src/snake/resources", "Executable JAR/"
'Copy JAR to JAR folder
fso.CopyFile "Snake/dist/Snake.JAR", "Executable JAR/"