<?php
  # Latitude, longitude
   if( $_GET["lat"] && $_GET["lon"] ) { 
      echo json_encode(array( // Return data
        'data' => 'test1'
      ));
   } 
?>
