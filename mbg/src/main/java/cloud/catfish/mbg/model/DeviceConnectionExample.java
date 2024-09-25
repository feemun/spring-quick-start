package cloud.catfish.mbg.model;

import java.util.ArrayList;
import java.util.List;

public class DeviceConnectionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DeviceConnectionExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andDeviceIdIsNull() {
            addCriterion("device_id is null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIsNotNull() {
            addCriterion("device_id is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdEqualTo(String value) {
            addCriterion("device_id =", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotEqualTo(String value) {
            addCriterion("device_id <>", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThan(String value) {
            addCriterion("device_id >", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThanOrEqualTo(String value) {
            addCriterion("device_id >=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThan(String value) {
            addCriterion("device_id <", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThanOrEqualTo(String value) {
            addCriterion("device_id <=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLike(String value) {
            addCriterion("device_id like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotLike(String value) {
            addCriterion("device_id not like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIn(List<String> values) {
            addCriterion("device_id in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotIn(List<String> values) {
            addCriterion("device_id not in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdBetween(String value1, String value2) {
            addCriterion("device_id between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotBetween(String value1, String value2) {
            addCriterion("device_id not between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdIsNull() {
            addCriterion("neighbour_device_id is null");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdIsNotNull() {
            addCriterion("neighbour_device_id is not null");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdEqualTo(String value) {
            addCriterion("neighbour_device_id =", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdNotEqualTo(String value) {
            addCriterion("neighbour_device_id <>", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdGreaterThan(String value) {
            addCriterion("neighbour_device_id >", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdGreaterThanOrEqualTo(String value) {
            addCriterion("neighbour_device_id >=", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdLessThan(String value) {
            addCriterion("neighbour_device_id <", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdLessThanOrEqualTo(String value) {
            addCriterion("neighbour_device_id <=", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdLike(String value) {
            addCriterion("neighbour_device_id like", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdNotLike(String value) {
            addCriterion("neighbour_device_id not like", value, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdIn(List<String> values) {
            addCriterion("neighbour_device_id in", values, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdNotIn(List<String> values) {
            addCriterion("neighbour_device_id not in", values, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdBetween(String value1, String value2) {
            addCriterion("neighbour_device_id between", value1, value2, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceIdNotBetween(String value1, String value2) {
            addCriterion("neighbour_device_id not between", value1, value2, "neighbourDeviceId");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayIsNull() {
            addCriterion("communication_way is null");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayIsNotNull() {
            addCriterion("communication_way is not null");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayEqualTo(Integer value) {
            addCriterion("communication_way =", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayNotEqualTo(Integer value) {
            addCriterion("communication_way <>", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayGreaterThan(Integer value) {
            addCriterion("communication_way >", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayGreaterThanOrEqualTo(Integer value) {
            addCriterion("communication_way >=", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayLessThan(Integer value) {
            addCriterion("communication_way <", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayLessThanOrEqualTo(Integer value) {
            addCriterion("communication_way <=", value, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayIn(List<Integer> values) {
            addCriterion("communication_way in", values, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayNotIn(List<Integer> values) {
            addCriterion("communication_way not in", values, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayBetween(Integer value1, Integer value2) {
            addCriterion("communication_way between", value1, value2, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andCommunicationWayNotBetween(Integer value1, Integer value2) {
            addCriterion("communication_way not between", value1, value2, "communicationWay");
            return (Criteria) this;
        }

        public Criteria andSendWayIsNull() {
            addCriterion("send_way is null");
            return (Criteria) this;
        }

        public Criteria andSendWayIsNotNull() {
            addCriterion("send_way is not null");
            return (Criteria) this;
        }

        public Criteria andSendWayEqualTo(Integer value) {
            addCriterion("send_way =", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayNotEqualTo(Integer value) {
            addCriterion("send_way <>", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayGreaterThan(Integer value) {
            addCriterion("send_way >", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayGreaterThanOrEqualTo(Integer value) {
            addCriterion("send_way >=", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayLessThan(Integer value) {
            addCriterion("send_way <", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayLessThanOrEqualTo(Integer value) {
            addCriterion("send_way <=", value, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayIn(List<Integer> values) {
            addCriterion("send_way in", values, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayNotIn(List<Integer> values) {
            addCriterion("send_way not in", values, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayBetween(Integer value1, Integer value2) {
            addCriterion("send_way between", value1, value2, "sendWay");
            return (Criteria) this;
        }

        public Criteria andSendWayNotBetween(Integer value1, Integer value2) {
            addCriterion("send_way not between", value1, value2, "sendWay");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitIsNull() {
            addCriterion("device_belong_to_unit is null");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitIsNotNull() {
            addCriterion("device_belong_to_unit is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitEqualTo(String value) {
            addCriterion("device_belong_to_unit =", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitNotEqualTo(String value) {
            addCriterion("device_belong_to_unit <>", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitGreaterThan(String value) {
            addCriterion("device_belong_to_unit >", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitGreaterThanOrEqualTo(String value) {
            addCriterion("device_belong_to_unit >=", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitLessThan(String value) {
            addCriterion("device_belong_to_unit <", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitLessThanOrEqualTo(String value) {
            addCriterion("device_belong_to_unit <=", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitLike(String value) {
            addCriterion("device_belong_to_unit like", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitNotLike(String value) {
            addCriterion("device_belong_to_unit not like", value, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitIn(List<String> values) {
            addCriterion("device_belong_to_unit in", values, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitNotIn(List<String> values) {
            addCriterion("device_belong_to_unit not in", values, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitBetween(String value1, String value2) {
            addCriterion("device_belong_to_unit between", value1, value2, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andDeviceBelongToUnitNotBetween(String value1, String value2) {
            addCriterion("device_belong_to_unit not between", value1, value2, "deviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitIsNull() {
            addCriterion("neighbour_device_belong_to_unit is null");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitIsNotNull() {
            addCriterion("neighbour_device_belong_to_unit is not null");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitEqualTo(String value) {
            addCriterion("neighbour_device_belong_to_unit =", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitNotEqualTo(String value) {
            addCriterion("neighbour_device_belong_to_unit <>", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitGreaterThan(String value) {
            addCriterion("neighbour_device_belong_to_unit >", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitGreaterThanOrEqualTo(String value) {
            addCriterion("neighbour_device_belong_to_unit >=", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitLessThan(String value) {
            addCriterion("neighbour_device_belong_to_unit <", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitLessThanOrEqualTo(String value) {
            addCriterion("neighbour_device_belong_to_unit <=", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitLike(String value) {
            addCriterion("neighbour_device_belong_to_unit like", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitNotLike(String value) {
            addCriterion("neighbour_device_belong_to_unit not like", value, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitIn(List<String> values) {
            addCriterion("neighbour_device_belong_to_unit in", values, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitNotIn(List<String> values) {
            addCriterion("neighbour_device_belong_to_unit not in", values, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitBetween(String value1, String value2) {
            addCriterion("neighbour_device_belong_to_unit between", value1, value2, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }

        public Criteria andNeighbourDeviceBelongToUnitNotBetween(String value1, String value2) {
            addCriterion("neighbour_device_belong_to_unit not between", value1, value2, "neighbourDeviceBelongToUnit");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}