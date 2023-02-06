const eventMap = new Map()

export default {
    addEvent(event, func) {
        if (eventMap.has(event)) {
            eventMap.get(event).push(func)
        } else {
            const funcList = []
            funcList.push(func)
            eventMap.set(event, funcList)
        }
    },

    emit(event, data) {
        if (eventMap.has(event)) {
            const funcList = eventMap.get(event)
            funcList.forEach(func => func(data))
        }
    }
}