<?php

  // array for JSON response
  $response = array();
  
  // check for required fields
  if (isset($_POST['__id']) && isset($_POST['increment'])) {
    $id = $_POST['__id'];
    $increment = $_POST['increment'];
    $servername = "sql3.freemysqlhosting.net";
    $username = "sql383327";
    $password = "jT8!eC1%";

    // Create connction
    $conn = new mysqli($servername, $username, $password, $username);

    //Check conection
    if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
      $uploadOk = 0;
      echo "\nConnection to database has failed.";
    }

    if ($increment == "increment") { 
      $max = "UPDATE MediaEntries SET __upAws = __upAws + 1 WHERE __id = $id"; 
    } else if ($increment == "decrement") {
      $max = "UPDATE MediaEntries SET __upAws = __upAws - 1 WHERE __id = $id"; 
    }

    $result = mysqli_query($conn, $max);

    //check for empty result
    if ($result) {
      // successfully updated
      $response["success"] = 1;
      $response["id"] = $id;

      // echoing json response
    } else {
      // id not found
      $response["success"] = 0;
      $response["message"] = "Id not found";
      $response["id"] = $id;
    }
  } else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field is missing";
    $response["id"] = $id;
  }

  echo json_encode($response);

?>
