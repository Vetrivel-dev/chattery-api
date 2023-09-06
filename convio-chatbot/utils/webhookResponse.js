
const formatResponseForWebhook= (texts, payload) => {

    messages = []
    texts.forEach(text => {
        messages.push(
            {
                text: {
                    text: [text],
                    redactedText: [text]
                },
                responseType: 'HANDLER_PROMPT',
                source: 'VIRTUAL_AGENT'
            }
        );
    });

    let responseData = {
        fulfillmentResponse: {
            messages: messages,
            payload: {"myresponse":payload}
        }
    };

    return responseData
};