$(function() {

  $('#url').keypress(function(event) {
    if (event.keyCode == '13') {
      $('#submit').click();
    }
  });

  $('#submit').ajaxError(function() {
    $('#result').text('Error');
    $('#qrcode').attr('src', '');
    $('#open').attr('href', '');
  });

  $('#submit').click(function() {
    var post_url;
    var url = $('#url').val();

    //Check customise key
    var ALLOW_CHARS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890_-";
    var custom = $('#key_user').val();
    var c;
    if (custom != '') {
      var length =  custom.length;
      if (length > 32) {
         alert('Length must be <= 32 characters');
         return;
      }

      for (var i=0; i < length; i++) {
         c = custom.charAt(i);
         if (ALLOW_CHARS.indexOf(c) == -1) {
           alert(c + ' not allowed');
           return;
         }
      }
      post_url = '/api/shorten?url=' + encodeURIComponent(url) + '&custom=' + encodeURIComponent(custom);
    } else {
      post_url = '/api/shorten?url=' + encodeURIComponent(url);       
    }
    //alert(post_url);
    $.post(post_url, function(key) {
      var shortUrl = window.location + key;

      if (key == 'DUPLICATE_KEY') {
         $('#result').text('Key has been chosen. Please select another key.');
         //$('#qrcode').attr('src','');
         //$('#open').attr('href', '');       
      } else {
        $('#result').text(shortUrl);
        $('#qrcode').attr('src', '/api/qrcode?url=' + encodeURIComponent(shortUrl));
        $('#open').attr('href', shortUrl);
      }
    });
  });

  $('#submit').click();
});


