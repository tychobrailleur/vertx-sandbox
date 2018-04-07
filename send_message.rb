require 'bunny'
require 'date'

conn = Bunny.new(hostname: 'localhost', admin: 'guest', password: 'guest')
conn.start

ch = conn.create_channel
x = Bunny::Exchange.new(ch, :direct, 'processit.incoming', durable: true, auto_delete: false)


merchants = [ '45355345345', '453553455567', '45355345864' ]
10.times do
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

  x.publish(data, routing_key: '')
end

conn.close
