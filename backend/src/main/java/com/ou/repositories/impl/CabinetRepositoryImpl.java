package com.ou.repositories.impl;

import com.ou.pojo.Cabinet;
import com.ou.pojo.Contract;
import com.ou.pojo.Item;
import com.ou.repositories.CabinetRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class CabinetRepositoryImpl implements CabinetRepository {

    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public void createCabinet(Cabinet cabinet) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (!isExitedCabinet(cabinet.getContract().getId()))
            session.save(cabinet);
    }
    @Override
    public List<Object[]> getAllCabinet(Map<String, String> params) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = b.createQuery(Object[].class);

        Root<Cabinet> cabinet = cq.from(Cabinet.class);

        Join<Cabinet, Item> itemJoin = cabinet.join("items", JoinType.LEFT);
        Root<Contract> contractRoot = cq.from(Contract.class);

        List<Predicate> predicates = new ArrayList<>();

        String active = params.get("active");
        if (active != null && !active.isEmpty()) {
            boolean ac = Boolean.parseBoolean(active);
            predicates.add(b.equal(cabinet.get("isActive"), ac));
        }

        predicates.add(b.equal(contractRoot.get("id"), cabinet.get("contract")));


        Expression<Long> itemCount = b.count(
                b.selectCase()
                        .when(b.isNull(itemJoin.get("receivedDate")), 0)
                        .otherwise(1)
        );

        cq.multiselect(
                cabinet.get("id"),
                cabinet.get("isActive"),
                contractRoot.get("id"),
                itemCount
        );

        cq.where(predicates.toArray(Predicate[]::new));

        cq.groupBy(contractRoot.get("id"), cabinet.get("id"));

        // Create and execute the query
        Query query = s.createQuery(cq);
        return (List<Object[]>) query.getResultList();
    }

    @Override
    public List<Item> getItemsByCabinetId(int cabinetId, Map<String, String> params) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = s.getCriteriaBuilder();

        CriteriaQuery<Item> cq = builder.createQuery(Item.class);
        Root item = cq.from(Item.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(item.get("cabinet"), cabinetId));

        String status = params.get("status");
        if(status != null && !status.isEmpty()) {
            if(status.equalsIgnoreCase("pending")) {
                predicates.add(builder.isNull(item.get("receivedDate")));
            } else if(status.equalsIgnoreCase("received")) {
                predicates.add(builder.isNotNull(item.get("receivedDate")));
            }
        }

        cq.where(predicates.toArray(Predicate[]::new));

        Order orderByReceivedDateDesc = builder.desc(item.get("receivedDate"));
        cq.orderBy(orderByReceivedDateDesc);

        Query query = s.createQuery(cq);
        return (List<Item>) query.getResultList();
    }

    @Override
    public Boolean isActiveCabinet(int cabinetId) {
        Session s = this.factoryBean.getObject().getCurrentSession();

        Query q = s.createQuery("SELECT A.isActive FROM Cabinet A WHERE A.id=:cabinetId");
        q.setParameter("cabinetId", cabinetId);

        return (Boolean) q.getSingleResult();
    }

    @Override
    public void closeExpiredContractCabinets() {
        Session s = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = s.getCriteriaBuilder();

        CriteriaQuery<Cabinet> cq = builder.createQuery(Cabinet.class);
        Root cabinet = cq.from(Cabinet.class);
        Root contractRoot = cq.from(Contract.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(contractRoot.get("id"), cabinet.get("contract")));
        predicates.add(builder.lessThanOrEqualTo(contractRoot.get("endedDate"), LocalDate.now()));

        cq.select(cabinet).where(predicates.toArray(Predicate[]::new));

        List<Cabinet> cabinets = s.createQuery(cq).getResultList();
        for (Cabinet c : cabinets) {
            c.setIsActive(false);
            s.update(c);
        }
    }

    @Override
    public Boolean isExitedCabinet(int contractId) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        Query q = s.createNamedQuery("Cabinet.findByContract_Id");
        q.setParameter("id", contractId);
        return  !q.getResultList().isEmpty();
    }
}
