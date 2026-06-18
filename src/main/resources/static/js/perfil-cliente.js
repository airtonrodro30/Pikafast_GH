(() => {
    function setReadonlyState(form, readonly) {
        form.querySelectorAll(".profile-input").forEach((input) => {
            if (readonly) {
                input.setAttribute("readonly", "readonly");
            } else {
                input.removeAttribute("readonly");
            }
        });
    }

    function snapshotValues(form) {
        form.querySelectorAll(".profile-input").forEach((input) => {
            input.dataset.originalValue = input.value;
        });
    }

    function restoreValues(form) {
        form.querySelectorAll(".profile-input").forEach((input) => {
            input.value = input.dataset.originalValue || "";
        });
    }

    function updateActions(form, editing) {
        const actions = form.querySelector(".form-actions");
        const editButton = document.querySelector(`.btn-enable-edit[data-form-id="${form.id}"]`);

        if (actions) {
            actions.classList.toggle("d-none", !editing);
        }

        if (editButton) {
            editButton.classList.toggle("d-none", editing);
        }
    }

    function initializeEditableForm(form) {
        const startEditing = form.dataset.startEditing === "true";
        snapshotValues(form);
        setReadonlyState(form, !startEditing);
        updateActions(form, startEditing);

        const cancelButton = form.querySelector(".btn-cancel-edit");
        if (cancelButton) {
            cancelButton.addEventListener("click", (event) => {
                event.preventDefault();
                restoreValues(form);
                setReadonlyState(form, true);
                updateActions(form, false);
            });
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll(".profile-form").forEach(initializeEditableForm);

        document.querySelectorAll(".btn-enable-edit").forEach((button) => {
            button.addEventListener("click", (event) => {
                event.preventDefault();
                const formId = button.dataset.formId;
                const form = document.getElementById(formId);
                if (!form) {
                    return;
                }

                snapshotValues(form);
                setReadonlyState(form, false);
                updateActions(form, true);
            });
        });
    });
})();
