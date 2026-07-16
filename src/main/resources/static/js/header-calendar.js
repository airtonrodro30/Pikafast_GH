import {showCalendar} from "./shared/calendar.js";

document.addEventListener("DOMContentLoaded", () => {
    const datePicker = document.getElementById("date-picker");
    const displayDate = document.getElementById("display-date");
    const calendarTrigger = document.querySelector(".order-date-outer-container");

    showCalendar(datePicker, displayDate, calendarTrigger);
});
