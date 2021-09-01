interface ConfPlusData {
    name?: string;
    note?: string;
    rule?: string;
    status?: string;
    type?: string;
    updatedAt?: string;
    updatedBy?: string;
    confVersionVos?: ConfVersionDetail[];
}

interface ConfPlusDataAdd {
    confAction: 'save' | 'update';
    note?: string;
    rule?: string;
    status?: string;
    type?: string;
    version?: string;
    differenceCluster: boolean;
    confVersionsClusterForms: ConfVersionDetail[];
}

interface ConfVersionDetail {
    name?: string | null;
    note?: string;
    status?: "ENABLED" | "DISABLED";
    type?: 'MAP' | 'LIST' | 'STRING' | 'INTEGER' | 'DOUBLE' | 'LONG';
    value?: string | null;
    version?: number;
    differenceCluster?: boolean;
    confVersionClusterVos?: ConfVersionClusterVos[];
}

interface ConfVersionDetailAdd {
    note?: string;
    status?: "ENABLED" | "DISABLED";
    type?: 'MAP' | 'LIST' | 'STRING' | 'INTEGER' | 'DOUBLE' | 'LONG';
    value?: string | null;
    version?: number;
    differenceCluster?: boolean;
    confVersionsClusterForms?: ConfVersionClusterVos[];
}

interface ConfVersionClusterVos {
    clusterName?: string | null;
    clusterValue?: string;
}

type ConfVersionStatus = "ENABLED" | "DISABLED";
