package org.example.ostheo_projet.event;

import org.example.ostheo_projet.model.Invoice;

public record InvoiceUpdatedEvent(Invoice invoice) {
}
