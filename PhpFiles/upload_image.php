<?php
 // array for JSON response
 $response = array();
  
 // check for required fields
 if (isset($_POST['portrait'])) {
    $portrait = $_POST['portrait'];
    $servername = "server170.web-hosting.com";
    $username = "awttwbdi_user";
    $password = "awtter();";
    $database = "awttwbdi_db";
  
    $target_dir = "../public_ftp/media/";
    // Create connction
    $conn = new mysqli($servername, $username, $password, $database);
    //Check conection
    if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
      $uploadOk = 0;
      echo "\nConnection to database has failed.";
    }

    $max = "SHOW TABLE STATUS LIKE 'MediaEntries'";

    $result = $conn->query($max)->fetch_assoc();

    $hi = $result['Auto_increment'];

    $target_url = $target_dir . ($hi);

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