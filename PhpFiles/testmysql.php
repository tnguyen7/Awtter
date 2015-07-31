<?PHP
$servername = "sql3.freemysqlhosting.net";
$username = "sql381296";
$password = "pE4!kY7%";

// Create connction
$conn = new mysqli($servername, $username, $password);

//Check conection

if ($conn->connect_error) {
	die("Connection faild: " . $conn->connect_error);
} else {
echo "Connected successfully";
}

?>
