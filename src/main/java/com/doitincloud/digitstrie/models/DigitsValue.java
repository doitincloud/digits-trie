package com.doitincloud.digitstrie.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.doitincloud.commons.Utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitsValue {

    private String digits;

    private Integer score;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonIgnore
    private Map<String, Object> mapValue = new LinkedHashMap<>();

    public DigitsValue() {
    }

    public DigitsValue(String digits, Map<String, Object> map) {
        this.digits = digits;
        map.forEach((k, v) -> mapValue.put(k, v));
    }

    public String getDigits() {
        return digits;
    }

    public void setDigits(String digits) {
        this.digits = digits;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @JsonIgnore
    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("digits", digits);
        mapValue.forEach((k, v) -> {
            if (k.equals("digits")) return;
            map.put(k, v);
        });
        if (createdAt != null) map.put("created_at", createdAt.getTime() / 1000);
        if (updatedAt != null) map.put("updated_at", updatedAt.getTime() / 1000);
        return map;
    }

    @JsonIgnore
    public Map<String, Object> getMapValue() {
        return mapValue;
    }

    public void setMapValue(Map<String, Object> mapValue) {
        this.mapValue = mapValue;
    }

    @JsonProperty("value")
    public String getValue() {
        return Utils.toJson(mapValue);
    }

    public void setValue(String value) {
        if (value == null || value.length() == 0) {
            mapValue.clear();
            return;
        }
        this.mapValue = Utils.toMap(value);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
