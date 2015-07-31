<?php
$servername = "sql3.freemysqlhosting.net";
$username = "sql381296";
$password = "pE4!kY7%";

$target_dir = "media/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$imageFileType = pathinfo($target_file,PATHINFO_EXTENSION);
$uploadOk = 1;

// Create connction
$conn = new mysqli($servername, $username, $password, $username);

//Check conection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
  $uploadOk = 0;
  echo "HI";
}

$max = "SELECT __id FROM MediaEntries ORDER BY __id DESC LIMIT 1";

$result = $conn->query($max)->fetch_assoc();

echo $result;

$target_url = $target_dir . ($result['__id'] + 1);

echo "$target_url";

// Check if image file is a actual image or fake image
if(isset($_POST["submit"])) {
    $check = getimagesize($_FILES["fileToUpload"]["tmp_name"]);
    if($check !== false) {
        echo "File is an image - " . $check["mime"] . ".";
        $uploadOk = 1;
    } else {
        echo "File is not an image.";
        $uploadOk = 0;
    }
}
// Check if file already exists
if (file_exists($target_url)) {
    echo "Sorry, file already exists.";
    $uploadOk = 0;
}
// Check file size
if ($_FILES["fileToUpload"]["size"] > 500000) {
    echo "Sorry, your file is too large.";
    $uploadOk = 0;
}
// Allow certain file formats
if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg"
&& $imageFileType != "gif" ) {
    echo "Sorry, only JPG, JPEG, PNG & GIF files are allowed.";
    $uploadOk = 0;
}

// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo "Sorry, your file was not uploaded.";
// if everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_url)) {
        echo "The file ". basename( $_FILES["fileToUpload"]["name"]). " has been uploaded.";
        $sql = "INSERT INTO MediaEntries (__createdAt, __upAws) VALUES (NOW(), 0)";
	if ($conn->query($sql) === TRUE) {
	  echo "New record created successfully <3";
	} else {
	  echo "Error: ". $sql . "<br>". $conn->error;
	}
    } else {
        echo "Sorry, there was an error uploading your file.";
    }
}
?> 
