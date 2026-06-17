import {showCalendar} from "./shared/calendar.js"

document.addEventListener("DOMContentLoaded", () => {
    // Referencias DOM
    // -- CALENDARIO
    const datePicker = document.getElementById("date-picker");
    const displayDate = document.getElementById("display-date");
    const calendarTrigger = document.querySelector(".order-date-outer-container");
    
    //Funciones
    showCalendar(datePicker, displayDate, calendarTrigger);
});