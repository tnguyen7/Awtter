<?php

  $filter = $_GET['filter'];
  $startPoint = $_GET['start'];

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

  if ($filter == "popularity") {

    $max = "SELECT * FROM MediaEntries ORDER BY __upAws DESC, __createdAt DESC LIMIT 15 OFFSET $startPoint"; 
    $result = mysqli_query($conn, $max);

    $response["animals"] = array();

    while ($row = mysqli_fetch_array($result, MYSQLI_BOTH)) {
      $animal = array();
      $animal["__id"] = $row["__id"];
      $animal["__upAws"] = $row["__upAws"];
      $animal["__createdAt"] = $row["__createdAt"];
      $animal["__portrait"] = $row["__portrait"];
    
      array_push($response["animals"], $animal);
    }

    //success
    $response["success"] = 1;

  } else if($filter == "date") {

    $response["animals"] = array();

    $total = $_GET['totalPics'];

    for ($index = $total - 1; $index >= 0; --$index) {
      $animalid = $_GET[(string)$index];
      $max = "SELECT * FROM MediaEntries WHERE __id = $animalid"; 

      $result = mysqli_query($conn, $max);
      $row = mysqli_fetch_array($result, MYSQLI_BOTH);

      $animal = array();
      $animal["__id"] = $row["__id"];
      $animal["__upAws"] = $row["__upAws"];
      $animal["__createdAt"] = $row["__createdAt"];
      $animal["__portrait"] = $row["__portrait"];
    
      array_push($response["animals"], $animal);
    }

    //success
    $response["success"] = 1;

  }

  // echoing json response
  echo json_encode($response);

?>