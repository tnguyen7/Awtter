<?php
 // array for JSON response
 $response = array();
  
 // check for required fields
 if (isset($_POST['portrait'])) {

    $portrait = $_POST['portrait'];
	$servername = "sql3.freemysqlhosting.net";
	$username = "sql383327";
	$password = "jT8!eC1%";
	
	$target_dir = "media/";

	// Create connction
	$conn = new mysqli($servername, $username, $password, $username);

	//Check conection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
		$uploadOk = 0;
		echo "\nConnection to database has failed.";
	}

	$max = "SELECT __id FROM MediaEntries ORDER BY __id DESC LIMIT 1";

	$result = $conn->query($max)->fetch_assoc();

	$hi = $result['__id'] + 1;

	$target_url = $target_dir . ($result['__id'] + 1);

	$base=$_POST['image'];
	$binary=base64_decode($base);
	header('Content-Type: bitmap; charset=utf-8');
	
	// Getting image type
	$finfo = finfo_open();
	$image_type = finfo_buffer($f, $binary, FILEINFO_MIME_TYPE);

	$file=fopen($target_url, 'wb');
	fwrite($file, $binary);
	fclose($file);

$sql = "INSERT INTO MediaEntries (__createdAt, __portrait) VALUES (NOW(), $portrait)";

	if ($conn->query($sql) === TRUE) {
		echo $hi;
	} else {
		echo "Error: ". $sql . "<br>". $conn->error;
	}
} else {
	echo "Error with receiving portrait boolean";
}

?>
