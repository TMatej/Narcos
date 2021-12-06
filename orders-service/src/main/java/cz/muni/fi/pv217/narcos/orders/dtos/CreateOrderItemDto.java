package cz.muni.fi.pv217.narcos.orders.dtos;

public class CreateOrderItemDto {
    private Long medicationId;
    private Integer count;

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
