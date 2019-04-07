require 'bunny'
require 'date'
require 'base64'

conn = Bunny.new(hostname: 'localhost', admin: 'guest', password: 'guest')
conn.start

ch = conn.create_channel
x = Bunny::Exchange.new(ch, :direct, '', durable: true, auto_delete: false)


merchants = [ '45355345345', '453553455567', '45355345864' ]
10_000.times do
  amount = (1 + rand(50))*100
  data =<<DATA
{
  "type": "payment",
  "merchant_id": "#{merchants.sample}",
  "amount": #{amount},
  "currency": "EUR",
  "date": "#{DateTime.now.iso8601(3)}"
}
DATA

  x.publish(Base64.urlsafe_encode64(data), routing_key: 'test.queue')
end

conn.close
