function doGet(e) {
    var events = getCalEvents('andmorecreater@gmail.com');
    Logger.log(events);
}
function getCalEvents(id) {  //カレンダーの本日のイベントを取得
    var cal = CalendarApp.getCalendarById(id);
    var events = cal.getEventsForDay(new Date());
    return events;
}