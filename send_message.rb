require 'bunny'

conn = Bunny.new(hostname: 'localhost', admin: 'guest', password: 'guest')
conn.start

ch = conn.create_channel
x = Bunny::Exchange.new(ch, :direct, 'processit.incoming', durable: true, auto_delete: false)

data =<<DATA
{
  "type": "payment",
  "merchant_id": "45355345345",
  "amount": 1000,
  "currency": "EUR",
  "date": "2018-04-02T09:05:04+00:00"
}
DATA

x.publish(data, routing_key: '')
conn.close
