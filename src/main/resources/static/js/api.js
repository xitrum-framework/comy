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

      //hidden result customize and button save
      //asgin value domain url and new result customize
      //alert($('#result_customize'));
      $('#result_customize').val(key);
      //alert(key);
      //hideCustomize(true);
    });
  });

  $('#submit').click();

  /* event click when user want to customize shorten url*/
  $('#btn_customize').click(function(){
     hideCustomize(false);
  });
  
  /* when user customize shorten url and click save let save to database */
  $('#btn_save').click(function(){
     var url = $('#url').val();
     var custom = $('#result_customize').val();

     if (custom == null || custom == '') {
       alert('Customize key can not blank.');
       return;
     }

     $.post('/api/shorten?url=' + encodeURIComponent(url) + '&custom=' + encodeURIComponent(custom), function(key) {
       if (key == 'DUPLICATE_KEY') {
         $('#duplicate').html('Key has been chosen. Please select another key.');
         $('#duplicate').show();
         return;
       }

       var shortUrl = window.location + key;
       $('#result').text(shortUrl);
       $('#qrcode').attr('src', '/api/qrcode?url=' + encodeURIComponent(shortUrl));
       $('#open').attr('href', shortUrl);

       hideCustomize(true);
       $('#btn_customize').hide();
     });
  });

  // hidden result customize, button save, button customize and domain url when initial
  hideCustomize(true);
  $('#domain_url').html("" + window.location);
});

function hideCustomize(b) {
  if (b == true) {
    $('#firstrow').show();
    $('#secondrow').hide();
  } else {
    $('#firstrow').hide();
    $('#secondrow').show();  
  }
}
