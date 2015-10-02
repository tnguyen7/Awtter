<?php
	$servername = "server170.web-hosting.com";
  	$username = "awttwbdi_user";
  	$password = "awtter();";
  	$database = "awttwbdi_db";

	//array for JSON response
	$response = array();

	//check for required fields
	if (isset($_POST['__auto'])) {
		$auto = $_POST['__auto'];

		// Create connction
    	$conn = new mysqli($servername, $username, $password, $database);
    	//Check conection
    	if ($conn->connect_error) {
      		die("Connection failed: " . $conn->connect_error);
      		$uploadOk = 0;
      		echo "\nConnection to database has failed.";
    	}

    	$max = "DELETE FROM Comments WHERE __auto = $auto";

    	$result = mysqli_query($conn, $max);

    	//check for empty result
    	if ($result) {
    		//successfully updated
    		$response["success"] = 1;
    		$response["auto"] = $auto;
    	} else {
    		//id not found
    		$response["success"] = 0;
			$response["message"] = "Id not found";
    		$response["auto"] = $auto;
    	}
	} else {
		//required field missing
		$response["success"] = 0;
		$response["message"] = "Required field is missing";
    	$response["auto"] = $auto;
	}

	//echo json response
	echo json_encode($response);
?>