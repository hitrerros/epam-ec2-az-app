function handleEmailFormSubmit({formId, inputId, messageId, endpoint}) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const email = document.getElementById(inputId).value;

        fetch(`${endpoint}?mail=${encodeURIComponent(email)}`, {
            method: "GET"
        })
            .then(response => response.text())
            .then(data => typeOutText(data))
            .catch(error => {
                typeOutText("error occured: " + error.text);
            });
    });
}

function uploadFileProcess() {
    const form = document.getElementById("uploadFileForm");
    if (!form) return;

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const fileInput = document.getElementById("uploadFile");
        const file = fileInput.files[0];

        if (!file) {
            typeOutText("No file selected.");
            return;
        }

         fetch(`/files/${encodeURIComponent(file.name)}`, {
            method: "POST",
            headers: {
                "Content-Type": file.type || "application/octet-stream"  // optional: set content type
            },
            body: file // sends raw binary data
        })
            .then(response => response.text())
            .then(data => typeOutText(data))
            .catch(error => {
                typeOutText("Error occurred: " + error.message);
            });
    });

 }

function typeOutText(text) {
    const messageDiv = document.getElementById("sharedOutput");
    if (!messageDiv) return;

    messageDiv.innerHTML = `<div id="terminalOutput"></div>`;
    const outputDiv = document.getElementById("terminalOutput");
    let i = 0;

    const cursor = document.createElement("span");
    cursor.classList.add("blinking-cursor");
    outputDiv.appendChild(cursor);

    const typeInterval = setInterval(() => {
        if (i < text.length) {
            cursor.insertAdjacentText("beforebegin", text.charAt(i));
            i++;
        } else {
            clearInterval(typeInterval);
        }
    }, 30);
}

document.addEventListener("DOMContentLoaded", function () {
    handleEmailFormSubmit({
        formId: "subscribeForm",
        inputId: "subscribeEmail",
        messageId: "subscribeMessage",
        endpoint: "/queues/subscribe"
    });

    handleEmailFormSubmit({
        formId: "unsubscribeForm",
        inputId: "unsubscribeEmail",
        messageId: "unsubscribeMessage",
        endpoint: "/queues/unsubscribe"
    });

    uploadFileProcess()

});

