<?php
	$servername = "server170.web-hosting.com";
  $username = "awttwbdi_user";
  $password = "awtter();";
  $database = "awttwbdi_db";

  // Create connction
  $conn = new mysqli($servername, $username, $password, $database);
    //Check conection
    if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
      $uploadOk = 0;
      echo "\nConnection to database has failed.";
    }

    //array for JSON response
    $response = array();
    $animalid = $_POST['__id'];
    $name = $_POST['__name'];
    $comment = $_POST['__comment'];

    $sql = "INSERT INTO Comments (__id, __name, __comment, __createdAt) VALUES ($animalid, '$name', '$comment', NOW())";

    if ($conn->query($sql) === TRUE) {
    	//success
    	$response["success"] = 1;
    } else {
    	$response["success"] = 0;
    }

    echo json_encode($response);
?>