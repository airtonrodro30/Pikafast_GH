function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function formatDateForDisplay(date) {
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

function parseInputDate(value) {
    if (!value) {
        return null;
    }

    const [year, month, day] = value.split("-").map(Number);
    if (!year || !month || !day) {
        return null;
    }

    return new Date(year, month - 1, day);
}

// Mostrar Calendario
export function showCalendar(datePicker, displayDate, calendarTrigger) {
    if (!datePicker || !displayDate) {
        return;
    }

    const currentDate = parseInputDate(datePicker.value) ?? new Date();
    datePicker.value = formatDateForInput(currentDate);
    displayDate.textContent = formatDateForDisplay(currentDate);

    if (calendarTrigger) {
        calendarTrigger.addEventListener("click", () => {
            try {
                datePicker.showPicker();
            } catch (err) {
                datePicker.click();
            }
        });
    }

    datePicker.addEventListener("change", (e) => {
        const date = parseInputDate(e.target.value);
        if (date) {
            displayDate.textContent = formatDateForDisplay(date);
        }
    });
}
