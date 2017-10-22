require 'vertx'
include Vertx


timer_id = Vertx.set_periodic(1000) do
  40.times do |i|
    puts "Sending message #{i}..."
    Vertx::EventBus.send('hello.world', "Hello world #{i}")
  end
end
