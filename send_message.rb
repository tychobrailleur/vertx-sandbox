require 'vertx'
include Vertx


timer_id = Vertx.set_periodic(1000) do
  5.times do |i|
    puts "Sending message #{i}..."
    Vertx::EventBus.send('hello.world', "Hello world #{i}")
    Vertx::EventBus.send('goodbye.world', "Goodbye world #{i}")
  end
end
