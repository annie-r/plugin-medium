package myToolWindow
/*
import layoutinspector.model.ViewNode
import layoutinspector.parser.DisplayInfoFactory
import layoutinspector.parser.ViewPropertyParser
class utilKotlin {
    fun createViewNode(
            parent: ViewNode?,
            data: String,
            skippedProperties: Collection<String>
    ): ViewNode {
        var data = data
        var delimIndex = data.indexOf('<')
        //if (delimIndex < 0) {
        //    throw IllegalArgumentException("Invalid format for ViewNode, missing @: $data")
        //}
        var name = data.substring(0, delimIndex)
        data = data.substring(delimIndex + 1)
        delimIndex = data.indexOf(' ')
        val hash = data.substring(0, delimIndex)
        val node = ViewNode(parent, name, hash)
        node.index = if (parent == null) 0 else parent!!.children.size

        if (data.length > delimIndex + 1) {
            loadProperties(node, data.substring(delimIndex + 1), skippedProperties)
            node.id = node.getProperty("mID", "id")!!.value
        }
        node.displayInfo = DisplayInfoFactory.createDisplayInfoFromNode(node)
        parent?.let {
            it.children.add(node)
        }
        return node
    }

    private fun loadProperties(
            node: ViewNode,
            data: String,
            skippedProperties: Collection<String>
    ) {
        var start = 0
        var stop: Boolean

        do {
            val index = data.indexOf('=', start)
            val fullName = data.substring(start, index)

            val index2 = data.indexOf(',', index + 1)
            val length = Integer.parseInt(data.substring(index + 1, index2))
            start = index2 + 1 + length

            if (!skippedProperties.contains(fullName)) {
                val value = data.substring(index2 + 1, index2 + 1 + length)
                val property = ViewPropertyParser.parse(fullName, value)

                node.properties.add(property)
                node.namedProperties[property.fullName] = property
                node.addPropertyToGroup(property)
            }

            stop = start >= data.length
            if (!stop) {
                start += 1
            }
        } while (!stop)

        node.properties.sort()
    }
}*/