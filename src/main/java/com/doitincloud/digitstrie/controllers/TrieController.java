package com.doitincloud.digitstrie.controllers;

import com.doitincloud.commons.Utils;
import com.doitincloud.digitstrie.services.DigitsTrieServices;
import com.doitincloud.rdbcache.configs.AppCtx;
import com.doitincloud.rdbcache.configs.PropCfg;
import com.doitincloud.rdbcache.exceptions.BadRequestException;
import com.doitincloud.rdbcache.exceptions.NotFoundException;
import com.doitincloud.rdbcache.models.KvIdType;
import com.doitincloud.rdbcache.models.KvPair;
import com.doitincloud.rdbcache.supports.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TrieController {

    private static DecimalFormat durationFormat = new DecimalFormat("#.######");

    @Value("${default.table:dt_number_info}")
    private String defaultTable;

    @Autowired
    DigitsTrieServices digitsTrieServices;

    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(value = {
            "/v1/get/{value}",
            "/v1/get/{value}/{opt}"
    }, method = RequestMethod.GET)
    public ResponseEntity<?> get(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> data = digitsTrieServices.get(tn.context, tn.table, tn.number);
        if (data != null && data.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return sendReponse(request, tn.context, data);
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(value = {
            "/v1/start-with/{value}",
            "/v1/start-with/{value}/{opt}"
    }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> allStartWith(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        int limit = 8;
        if (request.isUserInRole("ADMIN")) {
            limit = 64;
        } else if (request.isUserInRole("SUPER")) {
            limit = 0;
        }
        Map<String, Object> data = digitsTrieServices.allStartWith(tn.context, tn.table, tn.number, limit);
        if (data != null && data.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return sendReponse(request, tn.context, data);
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(value = {
            "/v1/longest-match/{value}",
            "/v1/longest-match/{value}/{opt}"
    }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> longestMatch(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> data = digitsTrieServices.longestMatch(tn.context, tn.table, tn.number);
        if (data != null && data.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return sendReponse(request, tn.context, data);
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @PreAuthorize("#oauth2.hasScope('write')")
    @RequestMapping(value = {
            "/v1/save/{value}",
            "/v1/save/{value}/{opt}"
    }, method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<?> save(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt,
            @RequestBody Map<String, Object> map) {
        TableNumber tn = prepare(request, value, opt);
        Utils.getExcutorService().submit(() ->
            digitsTrieServices.save(tn.context, tn.table, tn.number, map)
        );
        return sendReponse(request, tn.context, "OK");
    }

    @PreAuthorize("#oauth2.hasScope('write')")
    @RequestMapping(value = {
            "/v1/sync-save/{value}",
            "/v1/sync-save/{value}/{opt}"
    }, method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<?> syncSave(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt,
            @RequestBody Map<String, Object> map) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> data = digitsTrieServices.save(tn.context, tn.table, tn.number, map);
        return sendReponse(request, tn.context, data);
    }

    @PreAuthorize("#oauth2.hasScope('delete')")
    @RequestMapping(value = {
            "/v1/delete/{value}",
            "/v1/delete/{value}/{opt}"
    }, method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> delete(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Utils.getExcutorService().submit(() -> {
            boolean result = digitsTrieServices.delete(tn.context, tn.table, tn.number);
            if (!result) {
                KvPair pair = new KvPair(tn.context.getTraceId(), "trace");
                Map<String, Object> map = new LinkedHashMap<>();
                Long now = System.currentTimeMillis();
                map.put("timestamp", now);
                map.put("path", request.getRequestURI());
                map.put("error", "number " + tn.number + " with table " + tn.table +" not found");
                pair.setData(map);
                AppCtx.getKvPairRepo().save(pair);
            }
        });
        return sendReponse(request, tn.context, "OK");
    }

    @PreAuthorize("#oauth2.hasScope('delete')")
    @RequestMapping(value = {
            "/v1/sync-delete/{value}",
            "/v1/sync-delete/{value}/{opt}"
    }, method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> syncDelete(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        boolean result = digitsTrieServices.delete(tn.context, tn.table, tn.number);
        if (result) {
            return sendReponse(request, tn.context, "OK");
        }
        throw new NotFoundException("number " + tn.number + " with table " + tn.table +" not found");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_SUPER')")
    @RequestMapping(value = {
            "/v1/trace/{traceId}"
    }, method = RequestMethod.GET)
    public ResponseEntity<?> trace_get(
            HttpServletRequest request,
            @PathVariable("traceId") String traceId) {

        Context context = new Context();
        KvPair pair = AppCtx.getKvPairRepo().findById(new KvIdType(traceId, "trace"));
        if (pair != null) {
            return sendReponse(request, context, pair.getData());
        } else {
            return sendReponse(request, context, null);
        }
    }

    private TableNumber prepare(HttpServletRequest request, String value, Optional<String> opt) {

        TableNumber tn = new TableNumber();

        String rawNumber= null;
        if (opt.isPresent()) {
            if (Character.isLetter(value.charAt(0))) {
                tn.table = value;
                rawNumber = opt.get();
            } else {
                rawNumber = value;
                tn.table = opt.get();
            }
        } else {
            rawNumber = value;
        }
        if (!digitsTrieServices.hasTable(tn.table)) {
            throw new BadRequestException("invalid table");
        }
        StringBuffer numberBuffer = new StringBuffer();
        for (int i = 0; i < rawNumber.length(); i++) {
            char c = rawNumber.charAt(i);
            if (c < '0' || c > '9' ) {
                continue;
            }
            numberBuffer.append(c);
        }
        if (numberBuffer.length() == 0) {
            throw new BadRequestException("invalid number");
        }
        tn.number =  numberBuffer.toString();

        tn.context = new Context();
        if (PropCfg.getEnableMonitor()) tn.context.enableMonitor(request);

        return tn;
    }

    class TableNumber {
        String table = defaultTable;
        String number;
        Context context;
    }

    ResponseEntity<Map<String, Object>> sendReponse(HttpServletRequest request, Context context, Object o) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Long now = System.currentTimeMillis();
        map.put("timestamp", now);
        Long duration = context.getDuration();
        if (duration != null) {
            double db = ((double) duration) / 1000000000.0;
            map.put("duration", durationFormat.format(db));
        }
        Map<String, Object> data = null;
        if (o instanceof Map) {
            data = (Map<String, Object>) o;
        } else if (o == null || o instanceof String) {
            data = new LinkedHashMap<>();
            if (o != null) {
                data.put("message", o);
            }
        } else {
            data = Utils.toMap(o);
        }
        map.put("data", data);
        if (request.isUserInRole("ADMIN") || request.isUserInRole("SUPER")) {
            String traceId = context.getTraceId();
            if (traceId != null) {
                map.put("trace_id", traceId);
            }
        }
        return ResponseEntity.ok(map);
    }
}

