package cloud.catfish.api.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XlcDroneStatusExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public XlcDroneStatusExample() {
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

        public Criteria andUavPortIsNull() {
            addCriterion("uav_port is null");
            return (Criteria) this;
        }

        public Criteria andUavPortIsNotNull() {
            addCriterion("uav_port is not null");
            return (Criteria) this;
        }

        public Criteria andUavPortEqualTo(Long value) {
            addCriterion("uav_port =", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortNotEqualTo(Long value) {
            addCriterion("uav_port <>", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortGreaterThan(Long value) {
            addCriterion("uav_port >", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortGreaterThanOrEqualTo(Long value) {
            addCriterion("uav_port >=", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortLessThan(Long value) {
            addCriterion("uav_port <", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortLessThanOrEqualTo(Long value) {
            addCriterion("uav_port <=", value, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortIn(List<Long> values) {
            addCriterion("uav_port in", values, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortNotIn(List<Long> values) {
            addCriterion("uav_port not in", values, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortBetween(Long value1, Long value2) {
            addCriterion("uav_port between", value1, value2, "uavPort");
            return (Criteria) this;
        }

        public Criteria andUavPortNotBetween(Long value1, Long value2) {
            addCriterion("uav_port not between", value1, value2, "uavPort");
            return (Criteria) this;
        }

        public Criteria andAltitudeIsNull() {
            addCriterion("altitude is null");
            return (Criteria) this;
        }

        public Criteria andAltitudeIsNotNull() {
            addCriterion("altitude is not null");
            return (Criteria) this;
        }

        public Criteria andAltitudeEqualTo(Double value) {
            addCriterion("altitude =", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeNotEqualTo(Double value) {
            addCriterion("altitude <>", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeGreaterThan(Double value) {
            addCriterion("altitude >", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeGreaterThanOrEqualTo(Double value) {
            addCriterion("altitude >=", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeLessThan(Double value) {
            addCriterion("altitude <", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeLessThanOrEqualTo(Double value) {
            addCriterion("altitude <=", value, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeIn(List<Double> values) {
            addCriterion("altitude in", values, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeNotIn(List<Double> values) {
            addCriterion("altitude not in", values, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeBetween(Double value1, Double value2) {
            addCriterion("altitude between", value1, value2, "altitude");
            return (Criteria) this;
        }

        public Criteria andAltitudeNotBetween(Double value1, Double value2) {
            addCriterion("altitude not between", value1, value2, "altitude");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainIsNull() {
            addCriterion("battery_remain is null");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainIsNotNull() {
            addCriterion("battery_remain is not null");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainEqualTo(Double value) {
            addCriterion("battery_remain =", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainNotEqualTo(Double value) {
            addCriterion("battery_remain <>", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainGreaterThan(Double value) {
            addCriterion("battery_remain >", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainGreaterThanOrEqualTo(Double value) {
            addCriterion("battery_remain >=", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainLessThan(Double value) {
            addCriterion("battery_remain <", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainLessThanOrEqualTo(Double value) {
            addCriterion("battery_remain <=", value, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainIn(List<Double> values) {
            addCriterion("battery_remain in", values, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainNotIn(List<Double> values) {
            addCriterion("battery_remain not in", values, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainBetween(Double value1, Double value2) {
            addCriterion("battery_remain between", value1, value2, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andBatteryRemainNotBetween(Double value1, Double value2) {
            addCriterion("battery_remain not between", value1, value2, "batteryRemain");
            return (Criteria) this;
        }

        public Criteria andDroneNameIsNull() {
            addCriterion("drone_name is null");
            return (Criteria) this;
        }

        public Criteria andDroneNameIsNotNull() {
            addCriterion("drone_name is not null");
            return (Criteria) this;
        }

        public Criteria andDroneNameEqualTo(String value) {
            addCriterion("drone_name =", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameNotEqualTo(String value) {
            addCriterion("drone_name <>", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameGreaterThan(String value) {
            addCriterion("drone_name >", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameGreaterThanOrEqualTo(String value) {
            addCriterion("drone_name >=", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameLessThan(String value) {
            addCriterion("drone_name <", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameLessThanOrEqualTo(String value) {
            addCriterion("drone_name <=", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameLike(String value) {
            addCriterion("drone_name like", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameNotLike(String value) {
            addCriterion("drone_name not like", value, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameIn(List<String> values) {
            addCriterion("drone_name in", values, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameNotIn(List<String> values) {
            addCriterion("drone_name not in", values, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameBetween(String value1, String value2) {
            addCriterion("drone_name between", value1, value2, "droneName");
            return (Criteria) this;
        }

        public Criteria andDroneNameNotBetween(String value1, String value2) {
            addCriterion("drone_name not between", value1, value2, "droneName");
            return (Criteria) this;
        }

        public Criteria andFixTypeIsNull() {
            addCriterion("fix_type is null");
            return (Criteria) this;
        }

        public Criteria andFixTypeIsNotNull() {
            addCriterion("fix_type is not null");
            return (Criteria) this;
        }

        public Criteria andFixTypeEqualTo(Integer value) {
            addCriterion("fix_type =", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeNotEqualTo(Integer value) {
            addCriterion("fix_type <>", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeGreaterThan(Integer value) {
            addCriterion("fix_type >", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("fix_type >=", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeLessThan(Integer value) {
            addCriterion("fix_type <", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeLessThanOrEqualTo(Integer value) {
            addCriterion("fix_type <=", value, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeIn(List<Integer> values) {
            addCriterion("fix_type in", values, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeNotIn(List<Integer> values) {
            addCriterion("fix_type not in", values, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeBetween(Integer value1, Integer value2) {
            addCriterion("fix_type between", value1, value2, "fixType");
            return (Criteria) this;
        }

        public Criteria andFixTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("fix_type not between", value1, value2, "fixType");
            return (Criteria) this;
        }

        public Criteria andHdgIsNull() {
            addCriterion("hdg is null");
            return (Criteria) this;
        }

        public Criteria andHdgIsNotNull() {
            addCriterion("hdg is not null");
            return (Criteria) this;
        }

        public Criteria andHdgEqualTo(Double value) {
            addCriterion("hdg =", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgNotEqualTo(Double value) {
            addCriterion("hdg <>", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgGreaterThan(Double value) {
            addCriterion("hdg >", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgGreaterThanOrEqualTo(Double value) {
            addCriterion("hdg >=", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgLessThan(Double value) {
            addCriterion("hdg <", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgLessThanOrEqualTo(Double value) {
            addCriterion("hdg <=", value, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgIn(List<Double> values) {
            addCriterion("hdg in", values, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgNotIn(List<Double> values) {
            addCriterion("hdg not in", values, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgBetween(Double value1, Double value2) {
            addCriterion("hdg between", value1, value2, "hdg");
            return (Criteria) this;
        }

        public Criteria andHdgNotBetween(Double value1, Double value2) {
            addCriterion("hdg not between", value1, value2, "hdg");
            return (Criteria) this;
        }

        public Criteria andHeadingIsNull() {
            addCriterion("heading is null");
            return (Criteria) this;
        }

        public Criteria andHeadingIsNotNull() {
            addCriterion("heading is not null");
            return (Criteria) this;
        }

        public Criteria andHeadingEqualTo(Double value) {
            addCriterion("heading =", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingNotEqualTo(Double value) {
            addCriterion("heading <>", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingGreaterThan(Double value) {
            addCriterion("heading >", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingGreaterThanOrEqualTo(Double value) {
            addCriterion("heading >=", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingLessThan(Double value) {
            addCriterion("heading <", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingLessThanOrEqualTo(Double value) {
            addCriterion("heading <=", value, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingIn(List<Double> values) {
            addCriterion("heading in", values, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingNotIn(List<Double> values) {
            addCriterion("heading not in", values, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingBetween(Double value1, Double value2) {
            addCriterion("heading between", value1, value2, "heading");
            return (Criteria) this;
        }

        public Criteria andHeadingNotBetween(Double value1, Double value2) {
            addCriterion("heading not between", value1, value2, "heading");
            return (Criteria) this;
        }

        public Criteria andLatitudeIsNull() {
            addCriterion("latitude is null");
            return (Criteria) this;
        }

        public Criteria andLatitudeIsNotNull() {
            addCriterion("latitude is not null");
            return (Criteria) this;
        }

        public Criteria andLatitudeEqualTo(Double value) {
            addCriterion("latitude =", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeNotEqualTo(Double value) {
            addCriterion("latitude <>", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeGreaterThan(Double value) {
            addCriterion("latitude >", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeGreaterThanOrEqualTo(Double value) {
            addCriterion("latitude >=", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeLessThan(Double value) {
            addCriterion("latitude <", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeLessThanOrEqualTo(Double value) {
            addCriterion("latitude <=", value, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeIn(List<Double> values) {
            addCriterion("latitude in", values, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeNotIn(List<Double> values) {
            addCriterion("latitude not in", values, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeBetween(Double value1, Double value2) {
            addCriterion("latitude between", value1, value2, "latitude");
            return (Criteria) this;
        }

        public Criteria andLatitudeNotBetween(Double value1, Double value2) {
            addCriterion("latitude not between", value1, value2, "latitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeIsNull() {
            addCriterion("longitude is null");
            return (Criteria) this;
        }

        public Criteria andLongitudeIsNotNull() {
            addCriterion("longitude is not null");
            return (Criteria) this;
        }

        public Criteria andLongitudeEqualTo(Double value) {
            addCriterion("longitude =", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeNotEqualTo(Double value) {
            addCriterion("longitude <>", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeGreaterThan(Double value) {
            addCriterion("longitude >", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeGreaterThanOrEqualTo(Double value) {
            addCriterion("longitude >=", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeLessThan(Double value) {
            addCriterion("longitude <", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeLessThanOrEqualTo(Double value) {
            addCriterion("longitude <=", value, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeIn(List<Double> values) {
            addCriterion("longitude in", values, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeNotIn(List<Double> values) {
            addCriterion("longitude not in", values, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeBetween(Double value1, Double value2) {
            addCriterion("longitude between", value1, value2, "longitude");
            return (Criteria) this;
        }

        public Criteria andLongitudeNotBetween(Double value1, Double value2) {
            addCriterion("longitude not between", value1, value2, "longitude");
            return (Criteria) this;
        }

        public Criteria andSatellitesIsNull() {
            addCriterion("satellites is null");
            return (Criteria) this;
        }

        public Criteria andSatellitesIsNotNull() {
            addCriterion("satellites is not null");
            return (Criteria) this;
        }

        public Criteria andSatellitesEqualTo(Integer value) {
            addCriterion("satellites =", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesNotEqualTo(Integer value) {
            addCriterion("satellites <>", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesGreaterThan(Integer value) {
            addCriterion("satellites >", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesGreaterThanOrEqualTo(Integer value) {
            addCriterion("satellites >=", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesLessThan(Integer value) {
            addCriterion("satellites <", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesLessThanOrEqualTo(Integer value) {
            addCriterion("satellites <=", value, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesIn(List<Integer> values) {
            addCriterion("satellites in", values, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesNotIn(List<Integer> values) {
            addCriterion("satellites not in", values, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesBetween(Integer value1, Integer value2) {
            addCriterion("satellites between", value1, value2, "satellites");
            return (Criteria) this;
        }

        public Criteria andSatellitesNotBetween(Integer value1, Integer value2) {
            addCriterion("satellites not between", value1, value2, "satellites");
            return (Criteria) this;
        }

        public Criteria andVxIsNull() {
            addCriterion("vx is null");
            return (Criteria) this;
        }

        public Criteria andVxIsNotNull() {
            addCriterion("vx is not null");
            return (Criteria) this;
        }

        public Criteria andVxEqualTo(Double value) {
            addCriterion("vx =", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxNotEqualTo(Double value) {
            addCriterion("vx <>", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxGreaterThan(Double value) {
            addCriterion("vx >", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxGreaterThanOrEqualTo(Double value) {
            addCriterion("vx >=", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxLessThan(Double value) {
            addCriterion("vx <", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxLessThanOrEqualTo(Double value) {
            addCriterion("vx <=", value, "vx");
            return (Criteria) this;
        }

        public Criteria andVxIn(List<Double> values) {
            addCriterion("vx in", values, "vx");
            return (Criteria) this;
        }

        public Criteria andVxNotIn(List<Double> values) {
            addCriterion("vx not in", values, "vx");
            return (Criteria) this;
        }

        public Criteria andVxBetween(Double value1, Double value2) {
            addCriterion("vx between", value1, value2, "vx");
            return (Criteria) this;
        }

        public Criteria andVxNotBetween(Double value1, Double value2) {
            addCriterion("vx not between", value1, value2, "vx");
            return (Criteria) this;
        }

        public Criteria andVyIsNull() {
            addCriterion("vy is null");
            return (Criteria) this;
        }

        public Criteria andVyIsNotNull() {
            addCriterion("vy is not null");
            return (Criteria) this;
        }

        public Criteria andVyEqualTo(Double value) {
            addCriterion("vy =", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyNotEqualTo(Double value) {
            addCriterion("vy <>", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyGreaterThan(Double value) {
            addCriterion("vy >", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyGreaterThanOrEqualTo(Double value) {
            addCriterion("vy >=", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyLessThan(Double value) {
            addCriterion("vy <", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyLessThanOrEqualTo(Double value) {
            addCriterion("vy <=", value, "vy");
            return (Criteria) this;
        }

        public Criteria andVyIn(List<Double> values) {
            addCriterion("vy in", values, "vy");
            return (Criteria) this;
        }

        public Criteria andVyNotIn(List<Double> values) {
            addCriterion("vy not in", values, "vy");
            return (Criteria) this;
        }

        public Criteria andVyBetween(Double value1, Double value2) {
            addCriterion("vy between", value1, value2, "vy");
            return (Criteria) this;
        }

        public Criteria andVyNotBetween(Double value1, Double value2) {
            addCriterion("vy not between", value1, value2, "vy");
            return (Criteria) this;
        }

        public Criteria andVzIsNull() {
            addCriterion("vz is null");
            return (Criteria) this;
        }

        public Criteria andVzIsNotNull() {
            addCriterion("vz is not null");
            return (Criteria) this;
        }

        public Criteria andVzEqualTo(Double value) {
            addCriterion("vz =", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzNotEqualTo(Double value) {
            addCriterion("vz <>", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzGreaterThan(Double value) {
            addCriterion("vz >", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzGreaterThanOrEqualTo(Double value) {
            addCriterion("vz >=", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzLessThan(Double value) {
            addCriterion("vz <", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzLessThanOrEqualTo(Double value) {
            addCriterion("vz <=", value, "vz");
            return (Criteria) this;
        }

        public Criteria andVzIn(List<Double> values) {
            addCriterion("vz in", values, "vz");
            return (Criteria) this;
        }

        public Criteria andVzNotIn(List<Double> values) {
            addCriterion("vz not in", values, "vz");
            return (Criteria) this;
        }

        public Criteria andVzBetween(Double value1, Double value2) {
            addCriterion("vz between", value1, value2, "vz");
            return (Criteria) this;
        }

        public Criteria andVzNotBetween(Double value1, Double value2) {
            addCriterion("vz not between", value1, value2, "vz");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
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