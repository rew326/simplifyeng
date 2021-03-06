Clear-Host

$directory = $MyInvocation.MyCommand.Path.toString().substring(0, $MyInvocation.MyCommand.Path.toString().lastIndexOf('\'))


$complexFolder = $directory + '\..\..\3_getComplexities\exampleComplex\'
if ((Get-ChildItem $complexFolder).Count -eq 0)
{
	$complexList =Get-Content ($complexFolder + '..\outputComplex\complex.csv')
}
else
{
	$complexList = Get-Content ($complexFolder + 'complex.csv')
}

$collocationsList = Get-Content ($directory + '\..\..\2_annotateCorpusCollocations\outputCollocationsList\collocationsList.txt')

if ((Get-ChildItem ($directory + '\..\input')).Count -eq 0)
{
	$input = Get-Content ($directory + '\..\exampleInput\article.txt')
}
else
{
	$inputFolder = $directory + '\..\input'
	$inputFile = (Get-ChildItem -Path $inputFolder).fullname
	$input = Get-Content $inputFile
}

$pattern = '[^-a-zA-Z]'
$input = $input.toLower()
$input = $input -replace $pattern, " "
$input= $input -split " "

###
$collocationsInput = @()

$i = 0

while ($i -lt $input.Length)
{
    $word = $input[$i]
	$potentialColl = $input[$i] + " " + $input[$i + 1]
	if ($collocationsList -contains $potentialColl)
	{
		$i = $i + 2
		$word = "<collocation>" + $potentialColl + "</collocation>"   
	}
	else
	{
		$i = $i + 1
	}
	
	
	$collocationsInput += $word
}



###



$results = ""

$wordsMap = @{}

foreach ($line in $complexList)
{
	$lineArray = $line.Split(", ")
	
	$wordsMap.Add($lineArray[2] + $lineArray[3], $lineArray[0])
}

$upperThreshold = 0.0075
$lowerThreshold = 0.005

$results = ""

foreach ($word in $collocationsInput)
{
	if(!$wordsMap.ContainsKey($word) -and $word -ne "\\s+")
	{
		$word = "<complex>" + $word + "</complex>"
		Write-Host "not contained"
	}
	elseif ($wordsMap.$word -lt $upperThreshold -and $wordsMap.$word -gt $lowerThreshold)
	{
		$complex += 1
		$word = "<complex>" + $word + "</complex>"
	}
	
	if ($word -ne "<complex></complex>")
	{
		$results = $results + $word + "`n"
	}
}



###



if (Test-Path ($directory + '\..\results\output.txt'))
{
	Remove-Item ($directory + '\..\results\output.txt')
}
New-Item -path ($directory + '\..\results\output.txt') -type "file" -Value $results