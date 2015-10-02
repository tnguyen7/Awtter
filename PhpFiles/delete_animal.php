<?php
  // array for JSON response
  $response = array();
  
  // check for required fields
  if (isset($_POST['__id'])) {
    $id = $_POST['__id'];

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
     
    $max = "DELETE FROM MediaEntries WHERE __id = $id"; 
    $result = mysqli_query($conn, $max);
    
    //check for empty result
    if ($result) {
      unlink($_SERVER['DOCUMENT_ROOT']."/media/" + $id);
      // successfully updated
      $response["success"] = 1;
      $response["id"] = $id;
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
  // echoing json response
  echo json_encode($response);
?>