package com.coderxi.plugin.fakeplayer.network

import io.netty.channel.AbstractChannel
import io.netty.channel.Channel
import io.netty.channel.ChannelConfig
import io.netty.channel.ChannelMetadata
import io.netty.channel.ChannelOutboundBuffer
import io.netty.channel.ChannelPipeline
import io.netty.channel.ChannelPromise
import io.netty.channel.DefaultChannelConfig
import io.netty.channel.DefaultEventLoop
import io.netty.channel.EventLoop
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress

class FakeChannel(parent: Channel?, private val address: InetAddress?): AbstractChannel(parent) {
    private val config: ChannelConfig = DefaultChannelConfig(this)
    private val pipeline: ChannelPipeline = FakeChannelPipeline(this)
    override fun config(): ChannelConfig { config.isAutoRead = true; return config }
    override fun doBeginRead() {}
    override fun doBind(localAddress: SocketAddress?) {}
    override fun doClose() {}
    override fun doDisconnect() {}
    override fun doWrite(`in`: ChannelOutboundBuffer) { while (`in`.current() != null) { `in`.remove() } }
    override fun isActive(): Boolean = true
    override fun isCompatible(loop: EventLoop?): Boolean = true
    override fun isOpen(): Boolean = true
    override fun pipeline(): ChannelPipeline = pipeline
    override fun localAddress0(): SocketAddress = InetSocketAddress(address, 25565)
    override fun remoteAddress0(): SocketAddress = InetSocketAddress(address, 25565)
    override fun metadata(): ChannelMetadata = ChannelMetadata(true)
    override fun eventLoop(): EventLoop = DefaultEventLoop()
    override fun newUnsafe(): AbstractUnsafe = object : AbstractUnsafe() {
        override fun connect(remoteAddress: SocketAddress?, localAddress: SocketAddress?, promise: ChannelPromise) {
            safeSetSuccess(promise)
        }
    }
}