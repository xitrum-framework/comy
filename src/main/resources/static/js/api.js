$(function() {
  $('#submit').ajaxError(function() {
    $('#result').text('Error');
  });

  $('#submit').click(function() {
    var url = $('#url').val();
    $.post('/api?url=' + encodeURIComponent(url), function(key) {
      var shortUrl = window.location + key;
      $('#result').text(shortUrl);
      $('#test').attr('href', shortUrl);
    });
  });
});
