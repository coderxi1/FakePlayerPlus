package com.coderxi.plugin.fakeplayer.utils

import eu.okaeri.configs.schema.GenericsDeclaration.of
import eu.okaeri.configs.schema.GenericsPair
import eu.okaeri.configs.serdes.BidirectionalTransformer
import eu.okaeri.configs.serdes.SerdesContext

class RegexTransformer : BidirectionalTransformer<String, Regex>() {
    override fun getPair(): GenericsPair<String?, Regex?> = GenericsPair(of(String::class.java),of(Regex::class.java))
    override fun leftToRight(data: String, serdesContext: SerdesContext) = Regex(data)
    override fun rightToLeft(data: Regex, serdesContext: SerdesContext) = data.pattern
}