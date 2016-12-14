var stompClient = null;

window.onload = function() {
    $('#eventList').hide();
}

function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    if (connected) {
        $('#eventList').show();
    }
    else {
        $('#eventList').hide();
    }
    $('#events').html('');
}

function connect() {
    $.get('/connect', undefined, function() {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/event', function (event) {
                addEvent(event.body)
            });
        });
    });
}

function addEvent(event) {
    var eventObj = JSON.parse(event);
    var labelClass = eventObj.eventType === 'ENTRY_CREATE' ? 'label-success' : 'label-danger';
    $('#events').append('<tr><td><span class="label ' +labelClass + '">' + eventObj.eventType + '</span></td>' +
                        '<td>' + eventObj.absolutePath + '</td></tr>');
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log('Disconnected');
}

$(function () {
    $('form').on('submit', function (e) {
        e.preventDefault();
    });
    $('#connect').click(function() { connect(); });
    $('#disconnect').click(function() { disconnect(); });
});