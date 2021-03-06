Clear-Host
# list of collocations
Invoke-Expression ($MyInvocation.MyCommand.Path.toString().substring(0, $MyInvocation.MyCommand.Path.toString().lastIndexOf('\')) + '\scripts\parse-coll.ps1')
$directory = $MyInvocation.MyCommand.Path.toString().substring(0, $MyInvocation.MyCommand.Path.toString().lastIndexOf('\')) + '\..\'
$loc = Get-Content -Path ($directory + 'outputCollocationsList\collocationsList.txt')

$output = $directory + 'outputCollocationsCorpus\'
Write-Host $output
$corpusFiles = Get-ChildItem -Path ($directory + '..\corpus\inputCorpus\*')

if ($corpusFiles.count -eq 0)
{
	$corpusFiles = Get-ChildItem -Path ($directory + '..\corpus\exampleCorpus\*')
	Write-Host ExampleCorpus
}
else
{
	Write-Host OutputCorpus
}

$pattern = '[^a-zA-Z]'

foreach ($UTFile in $corpusFiles)
{
	$currentOutput = $output + $UTFile.Name
	$UTContent = (Get-Content $UTFile) + "`n"
	Write-Host $UTFile

	$UTContent = $UTContent.ToLower()
	$UTContent = $UTContent -replace $pattern, ' '
	$UTContent = $UTContent -replace '\s+', ' '
	$UTContent = $UTContent.split(' ')
	$potentialColl = ""
	
	$i = 0

	while ($i -lt $UTContent.Length)
	{
	    $word = $UTContent[$i]
		if ($word -eq "unk" -or $word -eq "s")
		{
			$i = $i + 1
		}
		else
		{
		    $potentialColl = $UTContent[$i] + " " + $UTContent[$i + 1]
			if ($loc -contains $potentialColl)
			{
				$i = $i + 2
				$word = "<collocation>" + $potentialColl + "</collocation>"   
				Write-Host $word
			}
			else
			{
				$i = $i + 1
			}
			Add-Content $currentOutput $word
		}
	}
}