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

  // array for JSON response
  $response = array();
  $startPoint = $_GET['start'];
  $animalid = $_GET['__id'];

  $max = "SELECT * FROM Comments WHERE __id = $animalid ORDER BY __createdAt DESC LIMIT 15 OFFSET $startPoint"; 
  $result = mysqli_query($conn, $max);

  $response["comments"] = array();

  while ($row = mysqli_fetch_array($result, MYSQLI_BOTH)) {
    $comment = array();
    $comment["__auto"] = $row["__auto"];
    $comment["__name"] = $row["__name"];
    $comment["__comment"] = $row["__comment"];
    $comment["__createdAt"] = $row["__createdAt"];
    
    array_push($response["comments"], $comment);
  }

  //success
  $response["success"] = 1;

  // echoing json response
  echo json_encode($response);

?>