function doGet(e) {
    var events = getCalEvents('andmorecreater@gmail.com');
    var json = eventsToJson(events);
    Logger.log(json);
    return ContentService.createTextOutput(json);
}
function getCalEvents(id) {  //カレンダーの本日のイベントを取得
    var cal = CalendarApp.getCalendarById(id);
    var events = cal.getEventsForDay(new Date());
    return events;
}
function eventsToJson(events) {
    var jsonArray = [];

    for(var i = 0; i < events.length; i++) {
        var event = {};
        event.title = events[i].getTitle();
        event.description = events[i].getDescription();
        if(!events[i].isAllDayEvent()){
            event.startTime = _HHmm(events[i].getStartTime());
            event.endTime = _HHmm(events[i].getEndTime());
        }
        jsonArray.push(event);
    }
    return JSON.stringify(jsonArray);
}

/* 時刻の表記をHH:mmに変更 */
function _HHmm(str) {
    return Utilities.formatDate(str, 'JST', 'HH:mm');
}