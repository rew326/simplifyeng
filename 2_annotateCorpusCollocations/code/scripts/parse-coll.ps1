Clear-Host
$scriptLocation = $MyInvocation.MyCommand.Path.toString()
$folderPath = $scriptLocation.substring(0, $scriptLocation.lastIndexOf('\')) + '\..\..\..\1_getCollocations\outputCollocations\'
$files = Get-ChildItem $folderPath | Measure-Object

if ($files.count -eq 0)
{
	$folderPath = $folderPath + '..\exampleCollocations'
	Write-Host "Example folder"
}
else
{
	Write-Host "Original folder"
}

$collocations = $folderPath + '\collocations.csv'
$coll = Get-Content $collocations

#6.650715773827003

$outputPath = $folderPath + '\..\..\2_annotateCorpusCollocations\outputCollocationsList\collocationsList.txt'
$result = ''
foreach ($line in $coll)
{
	$lineArray = $line.Split(',')
	if ($lineArray[1] -gt 6.65)
	{
		$result = $result + $lineArray[0].ToLower() + "`n"
	}
}

if (Test-Path $outputPath)
{
	Remove-Item $outputPath
}
New-Item -Path $outputPath -type "file" -Value $result