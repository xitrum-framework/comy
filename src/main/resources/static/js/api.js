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
    var url = $('#url').val();
    $.post('/api/shorten?url=' + encodeURIComponent(url), function(key) {
      var shortUrl = window.location + key;
      $('#result').text(shortUrl);
      $('#qrcode').attr('src', '/api/qrcode?url=' + encodeURIComponent(shortUrl));
      $('#open').attr('href', shortUrl);
    });
  });

  $('#submit').click();
});
