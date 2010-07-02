var ALLOW_CHARS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890_-";
// Returns true if the key is valid.
function validateKey(key) {
  var length = key.length;
  if (length > 32) {
    alert('Key must not be longer than 32 characters');
    return false;
  }

  for (var i = 0; i < length; i++) {
    var c = key.charAt(i);
    if (ALLOW_CHARS.indexOf(c) == -1) {
      alert("'" + c + "'" + ' is not allowed in key');
      return false;
    }
  }

  return true;
}

$(function() {
  $('#url, #key').keypress(function(event) {
    if (event.keyCode == '13') {
      $('#submit').click();
    }
  });

  $('#submit').click(function() {
    $('#submit').attr('disabled', 'disabled');

    var url = $('#url').val();
    var key = $('#key').val();

    var postUrl;
    if (key != '') {
      if (!validateKey(key)) {
        $('#submit').attr('disabled', '');
        return;
      }

      postUrl = '/api/shorten?url=' + encodeURIComponent(url) + '&key=' + encodeURIComponent(key);
    } else {
      postUrl = '/api/shorten?url=' + encodeURIComponent(url);
    }

    $.ajax({
      type: 'POST',
      url:  postUrl,
      success: function(key) {
        var shortUrl = window.location + key;
        $('#result').text(shortUrl);
        $('#qrcode').attr('src', '/api/qrcode?url=' + encodeURIComponent(shortUrl));
        $('#open').attr('href', shortUrl);
        $('#submit').attr('disabled', '');
      },
      error: function(xhr) {
        if (xhr.status == 401) {
          alert('The key has been chosen');
        } else if (xhr.status == 400) {
          alert('Invalid key');
        } else {
          alert('Server error');
        }
        $('#submit').attr('disabled', '');
      }

    });
  });

  // Demo
  $('#submit').click();
});
