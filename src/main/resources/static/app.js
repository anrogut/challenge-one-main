var stompClient = null;
var connected = false;

window.onload = function() {
    setConnected(connected);
}

function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    if (connected) {
        $('#events').show();
    }
    else {
        $('#events').hide();
    }
}

function connect() {
    $.get('/connect', undefined, function(id) {
        var socket = new SockJS('/ws');

        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            connected = true;
            setConnected(connected);
            console.log('Connected: ' + frame);
            console.log('Subscribing to: ' + '/topic/event/' + id)
            stompClient.subscribe('/topic/event/' + id, function (event) {
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
    connected = false
    setConnected(connected);
    console.log('Disconnected');
}

function addFile() {
    $.get('/addFile?name=' + $('#newFileNameInput').val());
}

$(function () {
    $('form').on('submit', function (e) {
        e.preventDefault();
    });
    $('#connect').click(function() { connect(); });
    $('#disconnect').click(function() { disconnect(); });
    $('#addFile').click(function() { addFile(); });
});