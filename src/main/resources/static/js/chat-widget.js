(function () {
    const widget = document.getElementById('pika-chat');

    if (!widget) {
        return;
    }

    const toggleButton = document.getElementById('pika-chat-toggle');
    const panel = document.getElementById('pika-chat-panel');
    const closeButton = document.getElementById('pika-chat-close');
    const messagesContainer = document.getElementById('pika-chat-messages');
    const form = document.getElementById('pika-chat-form');
    const textarea = document.getElementById('pika-chat-textarea');
    const sendButton = document.getElementById('pika-chat-send');
    const counter = document.getElementById('pika-chat-counter');
    const status = document.getElementById('pika-chat-status');

    const endpoint = widget.dataset.endpoint;
    const maxMessages = Number.parseInt(widget.dataset.maxMessages ?? '5', 10);
    const storageKey = 'pikafast.pika-chat.state';
    const cooldownMs = 60 * 1000;

    let userMessages = 0;
    let hasWelcomed = false;
    let isSending = false;
    let conversation = [];
    let cooldownUntil = null;
    let cooldownTimerId = null;

    function escapeCounterText() {
        counter.textContent = `${userMessages} de ${maxMessages} mensajes disponibles`;
    }

    function scrollToBottom() {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function setComposerState(disabled) {
        textarea.disabled = disabled;
        sendButton.disabled = disabled;
    }

    function showStatus(message) {
        status.textContent = message;
        status.hidden = false;
    }

    function hideStatus() {
        status.hidden = true;
        status.textContent = '';
    }

    function buildAvatar(src, alt) {
        const avatar = document.createElement('img');
        avatar.className = 'pika-chat__avatar';
        avatar.src = src;
        avatar.alt = alt;
        return avatar;
    }

    function saveState() {
        const state = {
            userMessages,
            hasWelcomed,
            isOpen: !panel.hidden,
            conversation,
            cooldownUntil
        };

        window.localStorage.setItem(storageKey, JSON.stringify(state));
    }

    function clearStoredState() {
        clearCooldownTimer();
        window.localStorage.removeItem(storageKey);
    }

    function resetChatState() {
        clearStoredState();
        userMessages = 0;
        hasWelcomed = false;
        isSending = false;
        conversation = [];
        cooldownUntil = null;
        messagesContainer.innerHTML = '';
        textarea.value = '';
        escapeCounterText();
        hideStatus();
        setComposerState(false);
    }

    function clearCooldownTimer() {
        if (cooldownTimerId !== null) {
            window.clearTimeout(cooldownTimerId);
            cooldownTimerId = null;
        }
    }

    function resetMessageQuota() {
        clearCooldownTimer();
        cooldownUntil = null;
        userMessages = 0;
        escapeCounterText();
        hideStatus();
        setComposerState(false);
        saveState();
    }

    function scheduleCooldownReset() {
        clearCooldownTimer();

        if (!cooldownUntil) {
            return;
        }

        const remainingMs = cooldownUntil - Date.now();

        if (remainingMs <= 0) {
            resetMessageQuota();
            return;
        }

        cooldownTimerId = window.setTimeout(resetMessageQuota, remainingMs);
    }

    function getCooldownMessage() {
        const remainingMs = Math.max((cooldownUntil ?? Date.now()) - Date.now(), 0);
        const remainingSeconds = Math.ceil(remainingMs / 1000);

        return `Has alcanzado el limite de ${maxMessages} mensajes disponibles. Espera ${remainingSeconds} segundos para continuar.`;
    }

    function addMessage(text, type, options = {}) {
        const { persist = true } = options;
        const message = document.createElement('div');
        message.className = `pika-chat__message pika-chat__message--${type}`;

        const bubble = document.createElement('div');
        bubble.className = 'pika-chat__bubble';
        bubble.textContent = text;

        if (type === 'ai') {
            message.appendChild(buildAvatar('/assets/img/logo.png', 'Pika AI'));
            message.appendChild(bubble);
        } else {
            message.appendChild(bubble);
            message.appendChild(buildAvatar('/assets/imag/icono_usuario.png', 'Usuario'));
        }

        messagesContainer.appendChild(message);
        scrollToBottom();

        if (persist) {
            conversation.push({ text, type });
            saveState();
        }
    }

    function restoreConversation(messages) {
        messagesContainer.innerHTML = '';
        conversation = [];

        messages.forEach(function (message) {
            addMessage(message.text, message.type, { persist: true });
        });
    }

    function loadState() {
        const rawState = window.localStorage.getItem(storageKey);

        if (!rawState) {
            return;
        }

        try {
            const state = JSON.parse(rawState);
            const savedMessages = Array.isArray(state.conversation) ? state.conversation : [];

            userMessages = Number.isInteger(state.userMessages) ? state.userMessages : 0;
            hasWelcomed = Boolean(state.hasWelcomed);
            cooldownUntil = Number.isInteger(state.cooldownUntil) ? state.cooldownUntil : null;
            restoreConversation(savedMessages);

            if (state.isOpen) {
                toggleButton.hidden = true;
                panel.hidden = false;
            }

            scheduleCooldownReset();
        } catch (error) {
            window.localStorage.removeItem(storageKey);
        }
    }

    function enforceLimit() {
        if (userMessages >= maxMessages) {
            if (!cooldownUntil || cooldownUntil <= Date.now()) {
                cooldownUntil = Date.now() + cooldownMs;
                scheduleCooldownReset();
            }

            setComposerState(true);
            showStatus(getCooldownMessage());
            saveState();
            return true;
        }

        cooldownUntil = null;
        setComposerState(false);
        hideStatus();
        return false;
    }

    async function fetchAIResponse(message) {
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        const response = await fetch(endpoint, {
            method: 'POST',
            headers,
            body: JSON.stringify({ message })
        });

        if (!response.ok) {
            throw new Error(`Chat API error: ${response.status}`);
        }

        return response.json();
    }

    function openChat() {
        toggleButton.hidden = true;
        panel.hidden = false;

        if (!hasWelcomed) {
            addMessage('Hola, soy Pika, tu asistente de IA\n¿Dime como puedo ayudarte hoy?', 'ai');
            hasWelcomed = true;
            saveState();
        }

        textarea.focus();
        scrollToBottom();
        saveState();
    }

    function closeChat() {
        resetChatState();
        panel.hidden = true;
        toggleButton.hidden = false;
    }

    toggleButton.addEventListener('click', openChat);
    closeButton.addEventListener('click', closeChat);

    document.querySelectorAll('form[action$="/logout"], form[action*="/logout"]').forEach(function (logoutForm) {
        logoutForm.addEventListener('submit', clearStoredState);
    });

    window.setInterval(function () {
        if (cooldownUntil && userMessages >= maxMessages) {
            showStatus(getCooldownMessage());
        }
    }, 1000);

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        if (isSending || userMessages >= maxMessages) {
            enforceLimit();
            return;
        }

        const message = textarea.value.trim();

        if (!message) {
            return;
        }

        addMessage(message, 'user');
        userMessages += 1;
        escapeCounterText();
        textarea.value = '';
        saveState();

        const limitReached = enforceLimit();
        isSending = true;
        if (!limitReached) {
            setComposerState(true);
        }

        try {
            const data = await fetchAIResponse(message);
            addMessage(data.content ?? 'No se recibio respuesta del asistente.', 'ai');
        } catch (error) {
            addMessage('No pude responder en este momento. Intenta nuevamente en unos segundos.', 'ai');
            if (!limitReached) {
                showStatus('Ocurrio un error al consultar la IA.');
            }
        } finally {
            isSending = false;
            if (!limitReached) {
                setComposerState(false);
                textarea.focus();
            }
        }
    });

    loadState();
    escapeCounterText();
    enforceLimit();

    if (!panel.hidden) {
        scrollToBottom();
    }
})();
