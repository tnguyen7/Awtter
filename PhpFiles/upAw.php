<?php
  // array for JSON response
  $response = array();
  
  // check for required fields
  if (isset($_POST['__id']) && isset($_POST['increment'])) {

    $id = $_POST['__id'];
    $increment = $_POST['increment'];
    
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
    if ($increment == "increment") { 
      $max = "UPDATE MediaEntries SET __upAws = __upAws + 1 WHERE __id = $id"; 
    } else if ($increment == "decrement") {
      $max = "UPDATE MediaEntries SET __upAws = __upAws - 1 WHERE __id = $id"; 
    } else if ($increment == "info") {
      $max = "SELECT __upAws FROM MediaEntries WHERE __id = $id";
    }

    $result = mysqli_query($conn, $max);
    
    if ($increment != "info") {
      //check for empty result
      if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["id"] = $id;
      } else {
        // id not found
        $response["success"] = 0;
        $response["message"] = "Id not found";
        $response["id"] = $id;
      }
    }  else {
      $response["message"] = 1;
      $response["upAws"] = $result;
    }
  } else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field is missing";
    $response["id"] = $id;
  }
  echo json_encode($response);
?>